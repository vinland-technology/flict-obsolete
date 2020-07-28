#!/bin/bash


#echo "Version: $VERSION"



check_exists()
{
    EXISTS=$(git tag | grep -e "^$VERSION$" | wc -l)
    printf "%-40s" " * version already exists as tag: "
    if [ $EXISTS -ne 0 ]
    then
        echo "ERROR. Version/tag $VERSION seem to exist, bailing out"
        exit 1
    fi
    echo "OK ----"
}

update_java()
{
    cat com/sandklef/compliance/utils/Version.java | sed "s,LICENSE_CHECKER_VERSION = \"[0-9a-zA-Z\.]*\",LICENSE_CHECKER_VERSION = \"$VERSION\",g"
}

check_java()
{
    JAVA_VERSION=$(cat com/sandklef/compliance/utils/Version.java | grep "LICENSE_CHECKER_VERSION" | cut -d "=" -f 2 | sed 's,["; ]*,,g')

    printf "%-40s" " * version in Java source code: " 
    if [ "$VERSION" != "$JAVA_VERSION" ]
    then
        echo "ERROR. Version in java source code ($JAVA_VERSION) differs from requested ($VERSION). Update the Java source code and try again"
        exit 2
    fi
    echo "OK"
}

check_docker()
{
    DOCKER_VERSION=$(cat docker/flict/Dockerfile | grep "FLICT_RELEASE=" | cut -d "=" -f 2 | sed 's,[ ]*,,')
    printf "%-40s" " * version in Dockerfile: " 
    if [ "$VERSION" != "$DOCKER_VERSION" ]
    then
        echo "ERROR. Version in Dockerfile ($DOCKER_VERSION) differs from requested ($VERSION). Update the Dockerfile (docker/flict/Dockerfile) and try again"
        exit 2
    fi
    echo "OK"

}

handle_ret()
{
    if [ $1 -ne 0 ]
    then
        echo "ERROR. Failed $2"
        echo "Check $LOG_FILE"
        exit $1
    fi
    echo "OK"
}

build()
{
    printf "%-40s" " * configure: "
    ./configure --devel >> $LOG_FILE 2>&1
    handle_ret $?  "make clean"

    printf "%-40s" " * clean: "
    make clean >> $LOG_FILE 2>&1
    handle_ret $?  "make clean"

    printf "%-40s" " * all: "
    make all >> $LOG_FILE 2>&1
    handle_ret $?  "make all"

    printf "%-40s" " * test: "
    make test >> $LOG_FILE 2>&1
    handle_ret $?  "make test"

    printf "%-40s" " * configure for dist: "
    ./configure --dist-installation >> $LOG_FILE 2>&1
    handle_ret $?  "make clean"

    printf "%-40s" " * all: "
    make all >> $LOG_FILE 2>&1
    handle_ret $?  "make all"

    printf "%-40s" " * dist: "
    make dist >> $LOG_FILE 2>&1
    handle_ret $?  "make dist"

    printf "%-40s" " * test dist: "
    make test-dist >> $LOG_FILE 2>&1
    handle_ret $?  "make dist"

    printf "%-40s" " * rename dist: "
    mv flict.zip flict-$VERSION.zip
    handle_ret $?  "mv flict.zip flict-$VERSION.zip"
    
}

do_check()
{
    echo "Checking before release:"
    check_exists  
    check_java
    check_docker
    echo
}

do_build()
{
    echo "Build"
    build
    echo
}


# not needed - done via web interface?
do_tag()
{
    echo "Git"
    printf "%-40s" " * tag: "
    git tag -a "$VERSION" -m "new release $VERSION" >> $LOG_FILE 2>&1
    handle_ret $?  "git tag"
    
    printf "%-40s" " * push tag: "
    git push --tags  >> $LOG_FILE 2>&1
    handle_ret $?  "git push tag"
}

don_dokker()
{
    echo "Docker"
    pushd docker/flict > /dev/null 2>&1

    IMAGE_NAME=sandklef/flict
    
    printf "%-40s" " * remove docker image: "
    docker image rm -f $IMAGE_NAME  >> $LOG_FILE 2>&1
    handle_ret $?  "remove image"

    printf "%-40s" " * build docker image: "
    docker build $(pwd) -t $IMAGE_NAME -t $IMAGE_NAME:latest -t $IMAGE_NAME:$VERSION  >> $LOG_FILE 2>&1
    handle_ret $?  "build image"

    printf "%-40s" " * run docker image: "
    docker run -it -v `pwd`/components/:/components $IMAGE_NAME >> $LOG_FILE 2>&1
    handle_ret $?  "run image"

    printf "%-40s" " * tag docker image: "
    docker tag $IMAGE_NAME:$VERSION $IMAGE_NAME:latest
    handle_ret $?  "run image"

    if [ "$DOCKER_PUSH" = "true" ]
    then
        printf "%-40s" " * pushd docker image: "
        docker push $IMAGE_NAME:$VERSION
        handle_ret $?  "docker push $IMAGE_NAME:$VERSION"
    fi
    popd  > /dev/null 2>&1
}


while [ "$1" != "" ]
do
    case "$1" in
        "--docker" | "-d")
            DOCKER=true
            ;;
        "--docker-push" | "-dp")
            DOCKER_PUSH=true
            DOCKER=true
            ;;
        *)
            break
            ;;
    esac
    shift
done

export VERSION="$1"

LOG_FILE=`pwd`/release-$VERSION.log

do_check
do_build
##### do_tag

if [ "$DOCKER" = "true" ]
then
    don_dokker
else
    echo "No docker stuff made"
fi

