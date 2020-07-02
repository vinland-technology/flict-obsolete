#!/bin/bash

INSTALL_DIR=$(dirname $(realpath $(which $0)) | sed 's,\/var\/test,,g')
COMPONENT_DIR="${INSTALL_DIR/var/test/components}"

test_combination_count()
{
    COMPONENT="$1"
    EXPECTED_COUNT=$2
    echo -n " * $(basename $COMPONENT): "
    ACTUAL_COUNT=$(${INSTALL_DIR}/bin/license-checker.sh \
                                 --debug-component-license \
                                 -cf "${INSTALL_DIR}/licenses/connections/sgl.json" \
                                 -l "${INSTALL_DIR}/licenses/json" \
                                 -c "${COMPONENT}"  | \
                       grep "^{" | sort -u | uniq | wc -l)
    if [ $ACTUAL_COUNT -ne $EXPECTED_COUNT ]
    then
        echo " Fail, expected $EXPECTED_COUNT but got $ACTUAL_COUNT"
        exit
    fi
    echo "OK"
}

test_combination_counts()
{
    echo "Testing combination count"
    test_combination_count "${COMPONENT_DIR}/var/test/components/simple.json" 1
    test_combination_count "${COMPONENT_DIR}/var/test/components/simple-dual.json" 2
    test_combination_count "${COMPONENT_DIR}/var/test/components/simple-many.json" 1
}

test_combination_counts
