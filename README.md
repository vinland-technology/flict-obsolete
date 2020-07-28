<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# FOSS License Checker

# Introduction

FOSS License Checker is a Free and Open Source Software tool to verify
license compliance in and between [_components_](#component). You can
use the tool to automate the license compliance in the compliance work
flow.

FOSS License Checker checks components, with a defined license and
with dependencies (themselves being components), so you need to tweak
your other tools into providing this information in the correct [format](#component). 

## Extensible and tweakable

FOSS License Checker does not come with any knowledge about certain
policies, licenses and their compatibilities. These things are
specified outside the program. By default the tool uses the licenses
that comes with the tool. This makes it easy to extend the program
with new licenses etc.

You can tweak the tool by providing:

* [_License_](#license) - name and SPDX short name for a license

* [_License graph_](#license_graph) - a graph of license compatibility

* [_Later_](#later) - with these you can define how to interpret a license with "or-later" (e g _GPL-2.0-or-later_)

* [_Policy_](#policy) - specify which licenses you would like to avoid and which are denied

Read more about input in the separate page (doc/input).

# Building and using

You can chose between two ways of installing and using this tool:

* native installation 

* docker (see doc/docker)

# Try it out with docker

We have a small script that you can use to create a directory and a
component. The script also instructs you how to do a scan of the
component, using either a normally install FOSS License Checker or a
docker image.

```
curl https://gitlab.com/sandklef/foss-license-checker/-/raw/primary/bin/demo.sh | bash
```

# Installation

## Using the FOSS License Checker Docker image

### Required tools

docker - see docker.io for information on how to install docker

### Get Foss License Checker docker image

```
docker pull sandklef/foss-license-checker
```

### Prepare to run the FOSS License Checker

Create a directory for the components

```
mkdir components
```

### Run FOSS License Checker

Put the components you want checked in the above created folder (```components```). 

```
docker run -it -v `pwd`/components/:/components sandklef/foss-license-checker 
```

### Example run

We're going to analyse a component called Super Awesome Program. The component is
specified in the JSON file ```example.json``` and the ```components```
directory.

Let's have a look at the ```components``` directory
```
$ tree --charset=ascii components/
components/
`-- example.json

0 directories, 1 file
```

The component itself looks like this:
```
$ cat components/example.json 
{
    "meta": {
        "software":"License Policy Checker",
        "version" : "0.1"
    },
    "component": 
    { "name":    "Super Awesome Program",
      "license": "GPL-2.0-or-later", 
      "dependencies": [
          { "name":    "Great library",
            "license": "MIT|BSD-3-Clause&GPL-2.0-only ", 
            "dependencies": [  ] 
          } 
      ] 
    } 
} 
```

Let's run the check:
```
$ docker run -it -v `pwd`/components/:/components sandklef/foss-license-checker 
FOSS License Checker - for use in docker

Check components:
===========================

  example
  -------------------------
   * compliance:   yes
   * convert report to: pdf html docx opendocument plain json 

```

You should be able to see the report in various formats in the ```components/report``` directory.

```
$ tree components/
components/
├── check-components.log
├── example.json
└── reports
    ├── example
    │   ├── report-example.docx
    │   ├── report-example.html
    │   ├── report-example.json
    │   ├── report-example.md
    │   ├── report-example.opendocument
    │   ├── report-example.pdf
    │   └── report-example.plain
    └── summary.log

2 directories, 10 files
```

## Native installation

### Required tools

* jq

* GNU Make

* JDK (Jav Development Kit) (7 or higher)

* bash

* wget

* Extra Java components will be download when you configure

* pdfunite

### Download prebuilt package

Download the latest released prebuilt package from from gitlab.com:

https://gitlab.com/sandklef/foss-license-checker/-/releases

### Build from source code

#### Required java components

* GSON (downloaded automatically with configure)

* Apache common (downloaded automatically with configure)

You can install required tools using the script:

~~~
./var/setup.sh
~~~

*Note: we're currently only supporting Debian, Ubuntu, Fedora, Redhat GNU/Linux. If you're usong something else, please manually install the required tools* 

### Build and install

~~~
./configure
make
make install
~~~

This installs FOSS License Checker in ```~/.local/``` so make sure to update your PATH variable to include ```~/.local/bin```. If you want to install to some other directory you can tweak the configure script (check out how with ```./configure --help```).

#### Example

The below checks license checker itself. The component License Checker
and its dependencies are specified in the file
`./meta/license-policy-checker.json` as found in the source code.

~~~
foss-license-checker.sh -c ./meta/foss-license-checker.json 
~~~





# Syntax

<a name="component"></a>
## Component (required)

Let's begin with an example component. Let's say we have a program
"Text mangler" (GPL-2.0-only) that uses a piece of software called
"libtext" (BSD-3-Clause). A component could be specified like this:

```
{
    "meta": {
        "software":"FOSS License Checker",
        "version":"0.1"
    },
    "component": {
        "name": "Text mangler",
        "license": "GPL-2.0-only",
        "dependencies": [
            {
                "name": "libtext",
                "license": "BSD-3-Clause",
                "dependencies": []
            }
        ]
    }
}
```

For now, the meta section is not used so we can continue with the component. A component has a:

```name``` - name of the component

```license``` - the SPDX identifer of the license

```dependencies``` - a list of other components that this component depends on

<a name="license"></a>
## License (built in or custom)

A license is specified using:

```name``` - full name (SPDX) of the license

```spdx``` - the SPDX identifer of the license. Can be an algebraic expression.

Here's an example:

```
{
    "meta": {
        "software":"FOSS License Checker",
        "version":"0.1"
    },
    "license": {
        "name":"GNU General Public License v2.0 or later",
        "spdx":"GPL-2.0-or-later"
    }
}
```

You can skip the meta section for now.

If you want to provide your own licenses you need to put them in a
directory and pass that directory to this tool using the option
```--license-dir```.

<a name="license_expressions"></a>
### License expressions

A license expression can consist of:

* *(* and *)*


* *&*

* *|*

and these are interpreted according to boolean algebra. A license expression example:

```
 MIT & (Apache-2.0 | BSD-3-Clause)
```

<a name="license_graph"></a>
## License graph (built in or custom)

To decide wether a license is compatible with another a graph of
license compatibilities is used. Bye default License checker uses
licenses as specified FLOSS License Compatibility Graph project. Using
this graph is recommended and if you lack some license we suggest you
get in contact with that project to update their graph. If you still
would like to use your own graph you can use the command line option
```--compatibility-file```.


<a name="policy"></a>
## Policy (no built in, optional)

With a policy file you can tell this tool which licenses you're not
allowing (denied) and which you preferr not to use (gray). Here's an
example policy file:

```
{
    "meta" : {
        "software":"FOSS License Checker",
        "version":"0.1"
    } ,
    "policy": {
        "allowlist": [],
        "graylist": [
            "BSD-3-Clause"
        ],
        "deniedlist": [
            "MIT",
            "Zlib"
        ]
    }
}
```

<a name="later"></a>
## License later defininitions (built in or custom)

Some licenses can be specifed saying "or-later", e g
GPL-2.0-or-later. You can provide a list of definitions for this tool
to decide how these licenses should be interpreted.

Let's start with a example:


```
{
    "meta": {
        "software":"FOSS License Checker",
        "type": "later-definitions",
        "version":"0.1"
    },
    "later-definitions": [
        {
            "spdx": "GPL-2.0-or-later",
            "later": [
                "GPL-3.0-only"
            ]
        }
    ]
}
```

As with previous example you can for now skip the meta section. A later definition is specified using:

```spdx``` - the license (SPDX short name) this later definition is valid for

``later``` - a list of licenses (SPDX short name) that the above license can be turned into

In the above example we state that GPL-2.0-or-later also can be "GPL-3.0-only". If you want to use your own later definition file or disable later definitions by providing an empty file you can use the option ```--later-file```.

# Exit code and reports

## Exit code

FOSS License Checker returns:

* ***0*** if your component is fully compliant,

* ***1*** if your component is compliant but there are licenses that you requested (with a policy) to avoid

* ***2*** if your component is not compliant

All other exit codes indicate some kind of error.

## Report

A report of the component's compliance is created. By default a short
text report is created. With the tools also comes a couple of Report
format that can be used.

## Report formats

### JSON

This is currently rewritten and not available.

### Markdown (pdf, html ..)

Using this format you can create txt, html, pdf and what format pandoc can create from markdown.

# License of the FOSS License Checker

FOSS License Checker is released under GPLv3 (https://www.gnu.org/licenses/gpl-3.0.en.html)

# The way FOSS License Checker works

Check out: (doc/how.md)

