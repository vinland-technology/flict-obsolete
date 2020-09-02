#!/bin/bash

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

IMAGE_NAME=sandklef/flict
CONTAINER_NAME=flict

build_image()
{
    docker build $(pwd) -t $IMAGE_NAME
}


run_image()
{
    if [ "$FORCE" = "true" ]
    then
        clean_containers
    fi
    docker run -it -v `pwd`/components/:/components --name $CONTAINER_NAME $IMAGE_NAME
}

clean_containers()
{
    for i in $(docker ps -a| grep -v CONTAINER | awk '{ print $1 }' ); do docker container rm $i; done
    for i in $(docker container ls -a | grep $CONTAINER_NAME | awk '{ print $1}' ); do docker container rm $i; done
}

clean_image()
{
    docker image rm $IMAGE_NAME
}

# options
while [ "$1" != "" ]
do
    case "$1" in
        "--force" | "-f")
            FORCE="true"
            ;;
        *)
            echo "nore more options"
            break
            ;;
    esac
    shift
done

# directive
case "$1" in
    "clean-all" | "ca")
        echo "Clean all $FORCE"
        ;;
    "clean-containers" | "cc")
        echo "Clean containers $FORCE"
        clean_containers
        ;;
    "clean-images" | "ci")
        echo "Clean image $FORCE"
        clean_image
        ;;
    "build" | "b")
        echo "build image $FORCE"
        build_image
        ;;
    "run" | "r")
        echo "run image $FORCE"
        run_image
        ;;
    *)
        echo "SYNTAX ERROR"
        exit 1
        ;;
esac

    