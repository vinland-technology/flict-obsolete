#!/bin/sh

COMPONENTS_DIR=/components
REPORTS_DIR=${COMPONENTS_DIR}/reports

FORMATS="pdf html docx opendocument plain json"
SUMMARY_LOG=${COMPONENTS_DIR}/reports/summary.log
LOG=${COMPONENTS_DIR}/check-components.log

if [ -f /.dockerenv ]
then
    IN_DOCKER=true
else
    IN_DOCKER=false
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
    COMPONENT_NAME="$(basename $1 | sed 's,\.json,,g')"


    log ""
    log "  $COMPONENT_NAME"
    log "  -------------------------"
    mkdir -p "$REPORTS_DIR/$COMPONENT_NAME"
    logn "   * compliance: "
    $FLC_BIN $LICENSE_ARGS $COMPAT_ARGS $LATER_ARGS -c $COMPONENT --markdown > "$REPORTS_DIR/$COMPONENT_NAME/report-${COMPONENT_NAME}.md"
    RES_STR=
    SUM_STR=
    if [ $? -eq 0 ]
    then
        RES_STR=" yes"
        SUM_STR="compliant"
    elif [ $? -eq 1 ]
    then
        RES_STR=" yes, with gray policy"
        SUM_STR="compliant, with gray policy"
    else
        RES_STR=" no"
        SUM_STR="not compliant"
    fi
    log " $RES_STR"

    summarize "$COMPONENT_NAME:$SUM_STR"
    
    logn "   * convert report to: "
    for fmt in $FORMATS
    do
        logn "$fmt "
        pandoc $REPORTS_DIR/$COMPONENT_NAME/report-${COMPONENT_NAME}.md -o $REPORTS_DIR/$COMPONENT_NAME/report-${COMPONENT_NAME}.$fmt
    done
    log
}


check_components()
{
    log "Check components:"
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
log " ***            FOSS License Checker               ***"
log " ***                                               ***"
log " *****************************************************"
log " * " 
log " * Information about FOSS License Checker:" 
log " *   gitlab.com/sandklef/foss-license-checker"
log " *   version:      $($FLC_BIN --version | head -1)"
log " * " 
log " * Information about current check:" 
log " *   date:   $(date)"
log " *   os:     $(uname -a)"
log " * " 
if [ "$IN_DOCKER" = "true" ]
then
    log " *   docker: yes" 
else
    log " *   docker: not used" 
fi
log " * " 
log " * ..... thanks for using FOSS License Checker" 
log " * " 
log ""
check_components
log " *" 
log " *" 
if [ $NR_COMPONENTS -eq 0 ]
then
    log " * No components could be found - no checks performed"
else
    log " * $NR_COMPONENTS components have been checked"
    log " * "
    log " * Reports are available in $COMPONENTS_DIR/reports"
    log " * "
fi
log " * "
log " * " 
log

