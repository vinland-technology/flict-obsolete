<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# FOSS License Checker

FOSS License Checker is a Free and Open Source Software compliance
tool aimed at detecting license violations either with or without a
policy. It assumes every component has a known license expression.

FOSS License Checker can also conclude outbound license(s) for your
component.

# Input

You need to provide the component to check for compliance.

You can also tweak the tool by providing:

* licenses

* license graph

* policy

Read more about input in the separate page (doc/input).

# Output

A report of the component's compliance is created. By default a short text report is created. With the tools also comes a couple of Report format that can be used.

## Report formats

### JSON

This is currently rewritten and not available.

### Markdown (pdf, html ..)

Using this format you can create txt, html, pdf and what format pandoc can create from markdown.

# License of the FOSS License Checker

FOSS License Checker is released under GPLv3 (https://www.gnu.org/licenses/gpl-3.0.en.html)

# Building and using

You can chose between two ways of installing and using this tool:

* native installation (see doc/native-install)

* docker (see doc/docker)

# Demonstration

We have a small script that you can use to create a directory and a
component. The script also instructs you how to do a scan of the
component, using either a normally install FOSS License Checker or a
docker image.

You can use the script in two different ways:

## Safe

```
curl -LJO https://gitlab.com/sandklef/foss-license-checker/-/raw/primary/bin/demo.sh
# inspect the script 
chmod a+d demo.sh
./demo.sh
```

## Easy but less safe

```
curl https://gitlab.com/sandklef/foss-license-checker/-/raw/primary/bin/demo.sh | bash
```


# The way FOSS License Checker works

Check out: (doc/how.md)

