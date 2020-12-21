#!/bin/sh

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

FORMATS="pdf html docx opendocument plain json"


if [ -f /.dockerenv ]
then
    IN_DOCKER=true
    COMPONENTS_DIR=/components
    FLICT_BIN=bin/flict
else
    IN_DOCKER=false
    COMPONENTS_DIR=./components
    FLICT_BIN=flict
fi

REPORTS_DIR=${COMPONENTS_DIR}/reports
LOG=${COMPONENTS_DIR}/check-components.log
SUMMARY_LOG=${COMPONENTS_DIR}/reports/summary.log

if [ -d $COMPONENTS_DIR ] || [ -h $COMPONENTS_DIR ] 
then
    :
else
    echo "ERROR - Directory \"$COMPONENTS_DIR\" is missing"
    exit 4
fi

log()
{
    echo "$*"
    echo "$*" >> $LOG
}

logn()
{
    echo -n "$*"
    echo -n "$*" >> $LOG
}

summarize()
{
    echo "date:$*" >> $SUMMARY_LOG
}

check_component()
{
    COMPONENT="$1"
    COMPONENT_FILE="$(basename $1 | sed 's,\.json,,g')"
    COMPONENT_NAME=$(jq '.component.name' $1 | sed 's,\",,g')

    log " "
    log " $COMPONENT_NAME ($COMPONENT_FILE.json)"
    log " -------------------------"
    mkdir -p "$REPORTS_DIR/$COMPONENT_FILE"
    logn "    compliant:         "
#echo    $FLICT_BIN $LICENSE_ARGS $COMPAT_ARGS $LATER_ARGS -c $COMPONENT --markdown ">" "$REPORTS_DIR/$COMPONENT_FILE/report-${COMPONENT_FILE}.md"
    $FLICT_BIN $LICENSE_ARGS $COMPAT_ARGS $LATER_ARGS -c $COMPONENT --markdown > "$REPORTS_DIR/$COMPONENT_FILE/report-${COMPONENT_FILE}.md"
    RET=$?
    RES_STR=
    SUM_STR=
    if [ $RET -eq 0 ]
    then
        RES_STR=" yes"
        SUM_STR="compliant"
    elif [ $RET -eq 1 ]
    then
        RES_STR=" yes, with gray policy"
        SUM_STR="compliant, with gray policy"
    elif [ $RET -eq 0 ]
    then
        RES_STR=" no"
        SUM_STR="not compliant"
    else
        RES_STR=" Error while executing flict"
        SUM_STR=" Undetermined"
    fi
    log " $RES_STR"

    summarize "$COMPONENT_FILE:$SUM_STR"
    
    logn "    report formatted to: "
    for fmt in $FORMATS
    do
        logn "$fmt "
        pandoc $REPORTS_DIR/$COMPONENT_FILE/report-${COMPONENT_FILE}.md -o $REPORTS_DIR/$COMPONENT_FILE/report-${COMPONENT_FILE}.$fmt
    done
    log
}


check_components()
{
    log
    log "Checking components:"
    log "==========================="
    mkdir -p $REPORTS_DIR
    NR_COMPONENTS=$(ls -1 $COMPONENTS_DIR/*.json | wc -l)
    ls $COMPONENTS_DIR/*.json | while read c
    do
        check_component "$c"
    done
}

if [ -f $SUMMARY_LOG ]
then
    mv "$SUMMARY_LOG" "$SUMMARY_LOG".old
fi

log ""
log " *****************************************************"
log " ***                                               ***"
log " ***        FOSS License Compatibility Tool        ***"
log " ***                                               ***"
log " *****************************************************"
log " * " 
log " * Information about FOSS License Compatibility Tool:" 
log " *   web site:     https://gitlab.com/sandklef/flict"
log " *   version:      $($FLICT_BIN --version | head -1)"
log " * " 
log " * Information about current check:" 
log " *   date:     $(date)"
log " *   os:       $(uname -a)"
log " *   args:     $*"
if [ "$IN_DOCKER" = "true" ]
then
    log " *   executed: run inside docker" 
else
    log " *   executed: run natively" 
fi
log " * " 
log " *****************************************************"
log "  "
check_components
log "" 
log "" 
log "Summary:"
log "==========================="
log "" 
if [ $NR_COMPONENTS -eq 0 ]
then
    log "No components could be found - no checks performed"
elif [ $NR_COMPONENTS -eq 1 ]
then
    log "$NR_COMPONENTS component have been checked. Report is available in $COMPONENTS_DIR/reports"
else
    log "$NR_COMPONENTS components have been checked. Reports are available in $COMPONENTS_DIR/reports"
fi
log "" 
log "..... thanks for using FOSS License Compatibility Tool" 
exit 0
