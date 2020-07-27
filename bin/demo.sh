#!/bin/bash

# for demo mode
COMPONENTS_DIR=components
DEMO_COMPONENT=$COMPONENTS_DIR/example.json
DOCKER_IMAGE=sandklef/foss-license-checker

FLC_NAME=foss-license-checker.sh
DEMO_BIN_DIR=$(dirname $0)
#echo "DEMO_BIN_DIR: $DEMO_BIN_DIR"
FLC_BIN=$DEMO_BIN_DIR/$FLC_NAME
which $FLC_NAME >/dev/null 2>&1
WHICH_RET=$?

if [ -x $FLC_BIN ] ||  [ $WHICH_RET -eq 0 ] 
then
    FLC_INSTALLED=true
    if [ ! -x $FLC_BIN ]
    then
        FLC_BIN=$(which $FLC_NAME)
    fi
else
    FLC_INSTALLED=false
fi


demo_create_component()
{

    printf "%-40s" " * Creating components dir: "
    mkdir -p $COMPONENTS_DIR
    if [ $? -ne 0 ] ; then echo "ERROR. Failed creating components dir ($COMPONENTS_DIR)" ; exit 1; fi
    echo "OK"

    printf "%-40s" " * Creating fake component: "
    cat << END_OF_JSON > $DEMO_COMPONENT
{
    "meta": {
        "software":"License Policy Checker",
        "version" : "0.1"
    },
    "component": 
    { "name":    "Super Awesome Program",
      "license": "GPL-2.0-or-later", 
      "dependencies": [
          { "name":    "Great library",
            "license": "MIT|BSD-3-Clause&GPL-2.0-only ", 
            "dependencies": [  ] 
          } 
      ] 
    } 
} 
END_OF_JSON
    if [ $? -ne 0 ] ; then echo "ERROR. Failed creating component" ; exit 1; fi
    echo "OK"
}


demo_run_info()
{
    echo
    echo "Running a license check"
    echo "================================================="
    echo
    echo "To run a license check: "
    echo "-------------------------------------------------"
    if [ "$FLC_INSTALLED" = "true" ]
    then
        echo " $FLC_BIN -c $DEMO_COMPONENT"
    else
        echo " install FOSS License checker"
    fi
    echo
    echo "To run a license check using docker image: "
    echo "-------------------------------------------------"
    echo " docker pull $DOCKER_IMAGE"
    echo " docker run -it -v \`pwd\`/$COMPONENTS_DIR/:/$COMPONENTS_DIR $DOCKER_IMAGE"
    echo
    echo
    echo "The results of the license checks can be found in:"
    echo " $COMPONENTS_DIR/reports"
    echo
}

demo()
{
    demo_create_component
    demo_run_info
}

demo


