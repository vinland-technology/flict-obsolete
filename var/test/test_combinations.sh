#!/bin/bash

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

#
# Script to test that components with license expressions are parsed correctly
# - uses the print debug component feature (--debug-component-license) in license checker 
# 

INSTALL_DIR=$(dirname $(realpath $(which $0)) | sed 's,\/var\/test,,g')
COMPONENT_DIR="${INSTALL_DIR/var/test/components}"

FAILS=0
SUCCS=0
TESTS=

#
# test_combination_count
# - argument 1: the component files
# - argument 2: how many (uniq!) component expressions are expected
#
test_combination_count()
{
    TESTS=$(( $TESTS + 1 ))
    COMPONENT="$1"
    EXPECTED_COUNT=$2
    printf " * %-50s" "$(basename $COMPONENT): "
    ACTUAL_COUNT=$(${INSTALL_DIR}/bin/license-checker.sh \
                                 --debug-component-license \
                                 -cf "${INSTALL_DIR}/licenses/connections/sgl.json" \
                                 -ld "${INSTALL_DIR}/licenses/json" \
                                 -c "${COMPONENT_DIR}/var/test/combination-components/${COMPONENT}"  | \
                       grep "^{" | sort -u | uniq | wc -l)
    if [ $ACTUAL_COUNT -ne $EXPECTED_COUNT ]
    then
        echo " Fail, expected $EXPECTED_COUNT but got $ACTUAL_COUNT"
        FAILS=$(( $FAILS + 1 ))
        return
    fi
    SUCCS=$(( $SUCCS + 1 ))
    
    echo "OK"
}

test_combination_counts()
{
    echo "Testing combination count"
    test_combination_count "simple.json" 1
    test_combination_count "simple-dual.json" 2
    test_combination_count "simple-later.json" 2
    test_combination_count "simple-many.json" 1
    test_combination_count "simple-dep.json" 1
    test_combination_count "simple-deps.json" 1
    test_combination_count "simple-dep-dual.json" 2
    test_combination_count "simple-dep-dual-later.json" 3
    test_combination_count "simple-dep-duals.json" 4
    test_combination_count "simple-dep-many.json" 1
    test_combination_count "simple-dep-manys.json" 1
    test_combination_count "semi.json" 2
    test_combination_count "semi-dep.json" 6
    test_combination_count "simple-dep-many-later.json" 2
}


test_combination_counts

echo "Tests: $TESTS"
echo " * success:  $SUCCS"
echo " * failures: $FAILS"

exit $FAILS

