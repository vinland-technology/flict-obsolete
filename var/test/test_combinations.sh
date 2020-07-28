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

TMP_FILE=/tmp/test_combination-$USER-$$.txt

#
# test_combination_count
# - argument 1: the component files
# - argument 2: how many (uniq!) component expressions are expected
#
test_combination_count()
{
    COMPONENT="$1"
    EXPECTED_COUNT=$2
    EXPECTED_GRAY_COUNT=$3
    EXPECTED_DENIED_COUNT=$4

    POLICY_ARGS="$5"
    
    printf " * %-50s\n" "$(basename $COMPONENT)"
    ${INSTALL_DIR}/bin/flict \
                                 -ld "${INSTALL_DIR}/share/licenses/json" \
                                 -cf "${INSTALL_DIR}/share/licenses/connections/compatibilities.json" \
                                 -lf "${INSTALL_DIR}/share/licenses/later/later-definitions.json" \
                                 -c "${COMPONENT_DIR}/var/test/combination-components/${COMPONENT}" \
                                 ${POLICY_ARGS} > $TMP_FILE

    
    ACTUAL_COUNT=$(cat $TMP_FILE | sed -n '/Allowed combinations/,/Allowed and gray/p' | grep -v -e "policy:" -e "compliant:" -e "Allowed combinations" -e "Allowed and gray" -e '^[\-]*$'  -e '^\[' -e '^\]'  | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$" | grep -v '^[ ]*,[ ]*$' | sort -u | uniq | wc -l)

    TESTS=$(( $TESTS + 1 ))
    printf  "   * %-45s" "actual count" 
    if [ $ACTUAL_COUNT -ne $EXPECTED_COUNT ]
    then
        echo " Fail, expected $EXPECTED_COUNT but got $ACTUAL_COUNT"
        echo "ACTUAL: $ACTUAL_COUNT ($EXPECTED_COUNT)"
        cat $TMP_FILE | sed -n '/Allowed combinations/,/Allowed and gray/p' | grep -v -e "policy:" -e "compliant:" -e "Allowed combinations" -e "Allowed and gray" -e '^[\-]*$'  -e '^\[' -e '^\]'  | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$"
        echo $TMP_FILE
        FAILS=$(( $FAILS + 1 ))
        cat $TMP_FILE
        exit
        return
    fi
    echo " OK"
    SUCCS=$(( $SUCCS + 1 ))
    
    TESTS=$(( $TESTS + 1 ))
    printf  "   * %-45s" "actual gray" 
    ACTUAL_GRAY=$(cat $TMP_FILE | sed -n '/Allowed and gray/,/Allowed but denied/p' | grep -v -e "policy:" -e "compliant:" -e "Allowed and gray" -e "Allowed but denied" -e '^[\-]*$'  -e '^\[' -e '^\]'  | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$" | grep -v '^[ ]*,[ ]*$' | sort -u | uniq | wc -l    )
    if [ $ACTUAL_GRAY -ne $EXPECTED_GRAY_COUNT ]
    then
        echo " Fail with gray, expected $EXPECTED_GRAY_COUNT but got $ACTUAL_GRAY gray listed"
        echo "ACTUAL: $ACTUAL_GRAY ($EXPECTED_GRAY_COUNT)"
        cat $TMP_FILE | sed -n '/Allowed and gray/,/Allowed but denied/p' | grep -v -e "policy:" -e "compliant:" -e "Allowed and gray" -e "Allowed but denied" -e '^[\-]*$'  -e '^\[' -e '^\]'  | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$" | grep -v '^[ ]*,[ ]*$' 
        FAILS=$(( $FAILS + 1 ))
        cat $TMP_FILE
        exit
        return
    fi
    echo " OK"
    SUCCS=$(( $SUCCS + 1 ))
    

#    echo $TMP_FILE
    
    TESTS=$(( $TESTS + 1 ))
    printf  "   * %-45s" "denied" 
    ACTUAL_DENIED=$(cat $TMP_FILE | sed -n '/Allowed but denied/,//p' | grep -v -e "policy:" -e "compliant:" -e "Allowed but denied" -e '^[\-]*$'  -e '^\[' -e '^\]' | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$" | grep -v '^[ ]*,[ ]*$' | sort -u | uniq | wc -l)

#    echo "$ACTUAL_DENIED -ne $EXPECTED_DENIED_COUNT"

    if [ $ACTUAL_DENIED -ne $EXPECTED_DENIED_COUNT ]
    then
        echo " Fail with denied, expected $EXPECTED_DENIED_COUNT but got $ACTUAL_DENIED denied listed"
        echo "ACTUAL: $ACTUAL_DENIED ($EXPECTED_DENIED_COUNT)"
        cat $TMP_FILE | sed -n '/Allowed but denied/,//p' | grep -v -e "policy:" -e "compliant:" -e "Allowed but denied" -e '^[\-]*$'  -e '^\[' -e '^\]' | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$" | grep -v '^[ ]*,[ ]*$' 
        echo "---000000000000000000000000000000000000----------------"
        cat $TMP_FILE | sed -n '/Allowed but denied/,//p' | grep -v -e "policy:" -e "compliant:" -e "Allowed but denied" -e '^[\-]*$'  -e '^\[' -e '^\]' | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$" | grep -v '^[ ]*,[ ]*$' | grep -v '^[ ]*,[ ]*$' | sort -u 
        echo "---000000000000000000000000000000000000----------------"
        cat $TMP_FILE | sed -n '/Allowed but denied/,//p' | grep -v -e "policy:" -e "compliant:" -e "Allowed but denied" -e '^[\-]*$'  -e '^\[' -e '^\]' | tr '\n' '#' | sed 's,component:,\n,g' | grep -v "^[ \t]*$" | grep -v '^[ ]*,[ ]*$' | grep -v '^[ ]*,[ ]*$' | sort -u | uniq | wc -l
        
        FAILS=$(( $FAILS + 1 ))
      #  cat $TMP_FILE
        exit
        return
    fi
    echo " OK"


    SUCCS=$(( $SUCCS + 1 ))
}

test_combination_counts()
{
    echo "Testing combination count"
    test_combination_count "simple.json" 1 0 0
    test_combination_count "simple-dual.json" 2 0 0
    test_combination_count "simple-later.json" 2 0 0
    test_combination_count "simple-many.json" 1 0 0
    test_combination_count "simple-dep.json" 1 0 0
    test_combination_count "simple-deps.json" 1 0 0
    test_combination_count "simple-dep-dual.json" 2 0 0
    test_combination_count "simple-dep-dual-later.json" 2 0 1
    test_combination_count "simple-dep-duals.json" 2 0 2
    test_combination_count "simple-dep-many.json" 1 0 0
    test_combination_count "simple-dep-manys.json" 0 0 1
    test_combination_count "semi.json" 2 0 0
}


test_combination_policy_counts()
{
    echo "Testing combination count with policies"

    # with policies, no GPL-3.0*
    test_combination_count "semi-dep.json" 4 0 2 " -p var/test/policies/no-gpl3.json "
    test_combination_count "simple-dep-many-later.json" 0 0 2 " -p var/test/policies/no-gpl3.json "

    # with policies, no GPL-2.0*
    test_combination_count "semi-dep.json" 1 0 5 " -p var/test/policies/no-gpl2.json "
    test_combination_count "simple-dep-many-later.json" 0 0 2 " -p var/test/policies/no-gpl2.json "

    # with policies, gray GPL-2.0*
    test_combination_count "semi-dep.json" 1 3 2 " -p var/test/policies/gray-gpl2.json "
    # there's 0 ALLOWED AND GRAY list 
    test_combination_count "simple-dep-many-later.json" 0 0 2 " -p var/test/policies/gray-gpl2.json "

}


test_combination_counts
echo
test_combination_policy_counts

echo "Tests: $TESTS"
echo " * success:  $SUCCS"
echo " * failures: $FAILS"

exit $FAILS

