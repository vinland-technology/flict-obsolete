#!/bin/bash

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

INSTALL_DIR=$(dirname $(realpath $(which $0)) | sed 's,\/bin,,g')

CLASSPATH="$INSTALL_DIR:$INSTALL_DIR/lib/org.json.jar:$INSTALL_DIR/lib/commons-cli-1.4.jar"
DEFAULT_ARGS=" --license-dir licenses/json "
CLASS="com.sandklef.compliance.cli.LicenseChecker"

java -cp "$CLASSPATH" "$CLASS" "$DEFAULT_ARGS" $*
