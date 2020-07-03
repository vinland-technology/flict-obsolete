#!/bin/bash

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

INSTALL_DIR=$(dirname $(realpath $(which $0)) | sed 's,\/bin,,g')

CLASSPATH="$INSTALL_DIR:$INSTALL_DIR/lib/gson-2.2.2.jar:$INSTALL_DIR/lib/commons-cli-1.4.jar"
DEFAULT_ARGS=" --license-dir ${INSTALL_DIR}/licenses/json "
CLASS="com.sandklef.compliance.cli.LicenseChecker"

# basic verification of json files
JSON_FILES=$(echo $* | tr ' ' '\n' | grep json )
for FILE in $JSON_FILES
do
 #   echo check file $FILE
    #    wc -l $FILE
    if [ -f $FILE ]
       then
           jq '.' $FILE >/dev/null 2>&1
           RET=$?
           if [ $RET -ne 0 ]
           then
               echo "$LINE does not seem to be a valid JSON file"
               exit 1
           fi
    fi
done


ARGS=
while [ "$1" != "" ]
do
    case "$1" in
        "--pdf")
            PDF_FILE=$2
            ARGS="$ARGS --markdown"
            TMP_MD=tmp.md
            rm $TMP_MD
            shift
            ;;
        "--connection-graph"|"-cg")
            CONNECTION_GRAPH=true
            ;;
        "--license-dir"|"-l")
            LICENSE_DIR=true
            ARGS="$ARGS \"$1\""
            ;;
        *)
            ARGS="$ARGS \"$1\""
            ;;
    esac
    shift
done #<<<"$HENRIK"

if [ "$LICENSE_DIR" = "true" ]
then
    DEFAULT_ARGS=""
fi

run()
{
    echo java -cp "$CLASSPATH" "$CLASS" "$DEFAULT_ARGS" $ARGS $* | sh
}



#echo THESE: $ARGS
if [ "$CONNECTION_GRAPH" = "true" ]
then
    TMP_DOT=tmp.dot
    rm -f $TMP_DOT
    FILE_NAME=license-connections
    run -cg -o ${FILE_NAME}.dot &&  dot -Tpdf ${FILE_NAME}.dot  > ${FILE_NAME}.pdf
    echo created ${FILE_NAME}.pdf
elif [ "$TMP_MD" != "" ]
then
    run > $TMP_MD
    pandoc $TMP_MD -o tmp.pdf
    compliance-tool.sh libcairo2
    pdfunite tmp.pdf libcairo2.pdf report.pdf
    evince report.pdf
else
    run
fi
