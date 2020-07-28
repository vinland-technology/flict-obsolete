#!/bin/bash

# for demo mode
COMPONENTS_DIR=components
DEMO_COMPONENT=$COMPONENTS_DIR/example.json
DOCKER_IMAGE=sandklef/flict

FLICT_NAME=flict
DEMO_BIN_DIR=$(dirname $0)
#echo "DEMO_BIN_DIR: $DEMO_BIN_DIR"
FLICT_BIN=$DEMO_BIN_DIR/$FLICT_NAME
which $FLICT_NAME >/dev/null 2>&1
WHICH_RET=$?

if [ -x $FLICT_BIN ] ||  [ $WHICH_RET -eq 0 ] 
then
    FLICT_INSTALLED=true
    if [ ! -x $FLICT_BIN ]
    then
        FLICT_BIN=$(which $FLICT_NAME)
    fi
else
    FLICT_INSTALLED=false
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
        "software":"FOSS License Compatibility Tool",
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
    if [ "$FLICT_INSTALLED" = "true" ]
    then
        echo " $FLICT_BIN -c $DEMO_COMPONENT"
    else
        echo " you first need to install FOSS License checkerand/or make sure you the path to the tool is in your PATH variable"
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


