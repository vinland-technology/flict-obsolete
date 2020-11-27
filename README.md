<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# flict / FOSS License Compatibility Tool

# Introduction

***FOSS License Compatibility Tool*** (***flict***) is a Free and Open
Source Software tool to verify license compliance in and between
[_components_](#component). You can use the tool to automate license
compliance verification in the compliance work flow.

flict checks components, with a defined license and with dependencies
(themselves being components), provided in the flict [format](#component).

## Extensible and tweakable

flict does not come with any knowledge about certain policies,
licenses and their compatibilities. These things are specified outside
the tool. By default flict has files defining licenses and
compatibilities which probably gets most of our users going. Having
licenses and compatibilities (and even more stuff) defined outside the
tool makes it easy to extend the tool with new licenses etc without
modifying the code.

You can tweak the tool by providing:

* [_License_](#license) - change supported licenses, by specifying name and SPDX short name for a license

* [_License graph_](#license_graph) - change the license compatibility, by adding a graph of license compatibility

* [_Later_](#later) - change the interpretation of a license with "or-later" (e g _GPL-2.0-or-later_)

* [_Policy_](#policy) - specify which licenses you would like to avoid and which are denied

Read more about input in the separate page (doc/input).

# Building and using

You can chose between two ways of installing and using this tool:

* [_Native installation_](#native)

* [_Docker image_](#docker_image)

# Try it out with docker

```
mkdir components
curl https://gitlab.com/sandklef/flict/-/raw/primary/meta/flict.json -o components/flict.json
docker pull sandklef/flict
docker run -it -v `pwd`/components/:/components sandklef/flict
```

<a name="install"></a>
# Installation

<a name="docker_image"></a>
## Using the flict Docker image

### Required tools

docker - see https://docker.io 

### Get flict docker image

```
docker pull sandklef/flict
```

### Prepare to run the flict

Create a directory for the components

```
mkdir components
```

Put the components you want checked in the above created folder (```components```). If you want to try out ```flict``` with a sample component, you can try the following command:

```
curl "https://gitlab.com/sandklef/flict/-/raw/primary/meta/flict.json" -o components/flict.json
```


### Run flict

```
docker run -it -v `pwd`/components/:/components sandklef/flict
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
        "software":"FOSS License Compatibility Tool",
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
$ docker run -it -v `pwd`/components/:/components sandklef/flict
FOSS License Compatibility Tool - for use in docker

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

<a name="native_install"></a>
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

https://gitlab.com/sandklef/flict/-/releases

### Build from source code

#### Required java components

* GSON (downloaded automatically with configure)

* Apache common (downloaded automatically with configure)

You can install required tools using the script:

~~~
./var/setup.sh
~~~

*Note: we're currently supporting Debian, Ubuntu, Fedora, Redhat GNU/Linux. If you're using something else, please manually install the required tools* 

### Build and install

~~~
./configure
make
make install
~~~

This installs flict in ```~/.local/``` so make sure to update your PATH variable to include ```~/.local/bin```. If you want to install to some other directory you can tweak the configure script (check out how with ```./configure --help```).

#### Example

The below checks floct itself. The component flict
and its dependencies are specified in the file
`./meta/flict.json` as found in the source code.

~~~
flict -c ./meta/flict.json 
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
        "software":"FOSS License Compatibility Tool",
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
        "software":"FOSS License Compatibility Tool",
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

## License compatibilty strategies

### License matrix

This is a new feature, mist likely replacing the graph below,
currently being implemented. More information soon.

### License graph (built in or custom)

To decide wether a license is compatible with another a graph of the
license compatibilities is created. By default flict uses licenses as
specified FLOSS License Compatibility Graph project. Using this graph
is recommended and if you're missing licenses we suggest you get in
contact with that project to update their graph. If you still would
like to use your own graph you can use the command line option
```--compatibility-file```.


<a name="policy"></a>
## Policy (no built in, optional)

With a policy file you can tell this tool which licenses you disallow
and which you prefer not to avoid. Here's an example policy file:

```
{
    "meta" : {
        "software":"FOSS License Compatibility Tool",
        "version":"0.1"
    } ,
    "policy": {
        "allowlist": [],
        "avoidlist": [
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

Some licenses can be specifed saying "or-later", e.g.
GPL-2.0-or-later. You can provide a list of definitions for this tool
to decide how these licenses should be interpreted.

Let's start with a example:


```
{
    "meta": {
        "software":"FOSS License Compatibility Tool",
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

```later``` - a list of licenses (SPDX short name) that the above license can be turned into

In the above example we state that GPL-2.0-or-later also can be "GPL-3.0-only". If you want to use your own later definition file or disable later definitions by providing an empty file you can use the option ```--later-file```.

# Exit code and reports

flict outputs a report as well as an exit code.

## Exit code

flict returns:

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

### Markdown

Using this format you can create txt, html, pdf and what format pandoc can create from markdown.

# Complementary tools

## check-components.sh

This script was initially written to be used, and still is used, with
our docker image but the script turned out to be useful for other
stuff. Create a directory or place yourself where you can find a
directory name ```components``` and simply run the scripts. The script
will go through all the components in the ```components``` directory
and create reports for you. The reports can be found in the directory
```components/reports/```.

## demo.sh

A simple script to create a diretory, named ```components```, with a
sample component defined in a file called ```example.json```.

# License of flict

flict is released under GPLv3 (https://www.gnu.org/licenses/gpl-3.0.en.html)

# How the flict works

Check out: (doc/how.md)

# FAQ

## I am having problems starting docker

I have a component in the components directory (```components/example.json```) but when I run docker I get:

```
$ docker run -it -v /tmp/components/:/components sandklef/flict:0.1-beta12

bin/check-components.sh: 33: bin/check-components.sh: cannot create /components/check-components.log: Permission denied
 *****************************************************
bin/check-components.sh: 33: bin/check-components.sh: cannot create /components/check-components.log: Permission denied
```
... and so on.

### Answer

You're probably having SELinux enabled. To work around this, run docker like this:

```
$ docker run -it -v /tmp/components/:/components:z sandklef/flict
```

*Note: there is now ':z' at the end of the ```-v``` option*

