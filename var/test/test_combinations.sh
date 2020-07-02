#!/bin/bash

INSTALL_DIR=$(dirname $(realpath $(which $0)) | sed 's,\/var\/test,,g')
COMPONENT_DIR="${INSTALL_DIR/var/test/components}"

FAILS=0
SUCCS=0
TESTS=

test_combination_count()
{
    TESTS=$(( $TESTS + 1 ))
    COMPONENT="$1"
    EXPECTED_COUNT=$2
    echo -n " * $(basename $COMPONENT): "
    ACTUAL_COUNT=$(${INSTALL_DIR}/bin/license-checker.sh \
                                 --debug-component-license \
                                 -cf "${INSTALL_DIR}/licenses/connections/sgl.json" \
                                 -l "${INSTALL_DIR}/licenses/json" \
                                 -c "${COMPONENT_DIR}/var/test/components/${COMPONENT}"  | \
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
    test_combination_count "simple-many.json" 1
    test_combination_count "simple-dep.json" 1
    test_combination_count "simple-deps.json" 1
    test_combination_count "simple-dep-dual.json" 2
    test_combination_count "simple-dep-duals.json" 4
    test_combination_count "simple-dep-many.json" 1
    test_combination_count "simple-dep-manys.json" 1
    test_combination_count "semi.json" 2
    test_combination_count "semi-dep.json" 6
}


test_combination_counts

echo "Tests: $TESTS"
echo " * succsess: $SUCCS"
echo " * failures: $FAILS"

exit $FAILS

