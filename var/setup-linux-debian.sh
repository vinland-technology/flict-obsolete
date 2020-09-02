#!/bin/bash

# SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later

PACKAGES="default-jdk jq wget make curl texlive-latex-recommended pandoc texlive-latex-base texlive-fonts-recommended texlive-extra-utils texlive-latex-extra"

sudo apt-get install $PACKAGES 