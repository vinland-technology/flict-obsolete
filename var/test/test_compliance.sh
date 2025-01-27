#!/bin/bash

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

#
# Script to test compliance for components with license expressions and dependencies
# 

INSTALL_DIR=$(dirname $(realpath $(which $0)) | sed 's,\/var\/test,,g')
COMPONENT_DIR="${INSTALL_DIR/var/test/components}"
TMP_FILE=/tmp/test-compliance-${USER}-$$.txt

FAILS=0
SUCCS=0
TESTS=

#
# test_combination_count
# - argument 1: the component files
# - argument 2: how many (uniq!) component expressions are expected
#
test_compliance()
{
    TESTS=$(( $TESTS + 1 ))
    COMPONENT="$1"
    EXPECTED_COUNT=$2
    EXPECTED_RET=$3
    printf " * %-50s" "$(basename $COMPONENT): "
    CMD="${INSTALL_DIR}/bin/flict \
                           -ld \""${INSTALL_DIR}/share/licenses/json\"" \
                           -cf "${INSTALL_DIR}/share/licenses/connections/compatibilities.json" \
                           -lf "${INSTALL_DIR}/share/licenses/later/later-definitions.json" \
                           -c  \""${COMPONENT_DIR}/var/test/compliance-components/${COMPONENT}\"" "
    $CMD > $TMP_FILE
    ACTUAL_RET=$?
    ACTUAL_COUNT=$(cat $TMP_FILE | grep "\* compliant:" | cut -d":" -f 2)

    if [ $ACTUAL_RET -ne $EXPECTED_RET ]
    then
        echo " Fail, expected return value $EXPECTED_RET but got $ACTUAL_RET"
        FAILS=$(( $FAILS + 1 ))
        echo "command line: $CMD"
        cat $TMP_FILE
        return
    fi

    if [ $ACTUAL_COUNT -ne $EXPECTED_COUNT ]
    then
        echo " Fail, expected $EXPECTED_COUNT but got $ACTUAL_COUNT"
        FAILS=$(( $FAILS + 1 ))
        echo "command line: $CMD"
        cat $TMP_FILE
        return
    fi
    SUCCS=$(( $SUCCS + 1 ))
    
    echo "OK"
}

test_compliances()
{
    echo "Testing compliance"
    test_compliance "simple.json" 1 0
    test_compliance "simple-dep.json" 1 0
    test_compliance "simple-deps.json" 1 0 
    test_compliance "simple-deps-false.json" 0 2
    test_compliance "simple-dual.json" 2 0
    test_compliance "simple-many.json" 1 0
    test_compliance "simple-dep-many.json" 1 0
    test_compliance "simple-dep-many-false.json" 0 2
    test_compliance "simple-dep-dual.json" 1 0
    test_compliance "simple-dep-dual-false.json" 0 2
    test_compliance "simple-dep-duals.json" 2 0 #  out of 4
    test_compliance "simple-dep-duals-false.json" 0 2
    test_compliance "semi.json" 2 0
    test_compliance "semi-dep.json" 2 0
    test_compliance "simple-dep-manys.json" 0 2
}

test_compliances

rm $TMP_FILE

echo "Tests: $TESTS"
echo " * success:  $SUCCS"
echo " * failures: $FAILS"

exit $FAILS

