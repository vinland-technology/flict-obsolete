#!/bin/bash

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

#
# Script to test that license expressions are parsed correctly
# - uses the print expression feature (-e) in license checker 
# 

INSTALL_DIR=$(dirname $(realpath $(which $0)) | sed 's,\/var\/test,,g')

FAILS=0
SUCCS=0
TESTS=

#
# test_expression
# - argument 1: the expression to test
# - argument 2: how the expression is expected to be printed (after parsing)
#
# 
test_expression()
{
    TESTS=$(( $TESTS + 1 ))
    EXPR="$1" 
    EXPECTED_EXPR=$2

    printf " * %-50s" $(echo "$EXPR:" | sed 's, ,,g')
    ACTUAL_EXPR=$(${INSTALL_DIR}/bin/license-checker.sh \
                                -ld "${INSTALL_DIR}/licenses/json" \
                                -e "$EXPR" | \
                      grep -A 10 "List of License Lists" | \
                      grep -v -e "List of License Lists" -e "^[\-]*$" -e "^\]*$" -e "^\[*$" | \
               sed -e 's, ,,g' | tr '\n' '#' )

    if [ "$ACTUAL_EXPR" != "$EXPECTED_EXPR" ]
    then
        echo -n "FAIL    " 
        echo "Expressions differ:"
        echo "   * expected: $EXPECTED_EXPR"
        echo "   * actual:   $ACTUAL_EXPR"
        FAILS=$(( $FAILS + 1 ))
        echo
        return        
    fi
    SUCCS=$(( $SUCCS + 1 ))
    echo "OK"
}

test_expressions()
{
    echo "Testing expressions"
    test_expression "GPL-2.0-only" "[GPL-2.0-only]#"
    test_expression "(GPL-2.0-only)" "[GPL-2.0-only]#"
    test_expression "GPL-2.0-only & BSD-3-Clause" "[GPL-2.0-only,BSD-3-Clause]#"
    test_expression "(GPL-2.0-only & BSD-3-Clause)" "[GPL-2.0-only,BSD-3-Clause]#"
    test_expression "GPL-2.0-only |   BSD-3-Clause" "[GPL-2.0-only]#[BSD-3-Clause]#"
    test_expression "GPL-2.0-only |   BSD-3-Clause & MIT" "[GPL-2.0-only]#[BSD-3-Clause,MIT]#"
    test_expression "GPL-2.0-only |   (BSD-3-Clause & MIT)" "[GPL-2.0-only]#[BSD-3-Clause,MIT]#"
    test_expression "GPL-2.0-only |   BSD-3-Clause & MIT | Apache-2.0" "[GPL-2.0-only]#[BSD-3-Clause,MIT]#[Apache-2.0]#"
    test_expression "(GPL-2.0-only |   BSD-3-Clause) & MIT | Apache-2.0" "[GPL-2.0-only,MIT]#[BSD-3-Clause,MIT]#[Apache-2.0]#"
    test_expression "(GPL-2.0-only |   BSD-3-Clause) & MIT & Apache-2.0" "[GPL-2.0-only,MIT,Apache-2.0]#[BSD-3-Clause,MIT,Apache-2.0]#"
    test_expression "(GPL-2.0-only |   BSD-3-Clause) & (MIT | Apache-2.0)" "[GPL-2.0-only,MIT]#[GPL-2.0-only,Apache-2.0]#[BSD-3-Clause,MIT]#[BSD-3-Clause,Apache-2.0]#"
    test_expression "GPL-2.0-or-later" "[GPL-2.0-or-later]#[GPL-3.0-only]#"
    test_expression "GPL-2.0-or-later|MIT" "[GPL-2.0-or-later]#[GPL-3.0-only]#[MIT]#"
    test_expression "GPL-2.0-or-later&MIT" "[GPL-2.0-or-later,MIT]#[GPL-3.0-only,MIT]#"
    test_expression "GPL-2.0-only&MIT" "[GPL-2.0-only,MIT]#"
    test_expression "GPL-2.0-only&MIT|BSD-3-Clause" "[GPL-2.0-only,MIT]#[BSD-3-Clause]#"
    test_expression "GPL-2.0-only&(MIT|BSD-3-Clause)&Apache-2.0" "[GPL-2.0-only,MIT,Apache-2.0]#[GPL-2.0-only,BSD-3-Clause,Apache-2.0]#"

}


test_expressions

echo "Tests: $TESTS"
echo " * success:  $SUCCS"
echo " * failures: $FAILS"

exit $FAILS

