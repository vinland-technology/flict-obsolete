<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# License Checker

License Checker is an Free and Open Source Software compliance tool
aimed at detecting license violations either with or without a
policy. It assumes every component has a known license state.

License Checker can also conclude outbound licenses for your component.

# Input

## Component (required)

Let's begin with an example component. Let's say we have a program
"Text mangler" (GPL-2.0-only) that uses a piece of software called
"libtext" (BSD-3-Clause). A component could be specified like this:

```
{
    "meta": {
        "software":"License Policy Checker",
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

## Licenses (built in or custom)

A license is specified using:

```name``` - full name (SPDX) of the license

```spdx``` - the SPDX identifer of the license. Can be an algebraic expression.

Here's an example:

```
{
    "meta": {
        "software":"License Policy Checker",
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

### License expressions

A license expression can consist of:

* *(* and *)*


* *&*

* *|*

and these are interpreted according to boolean algebra. A license expression example:

```
 MIT & (Apache-2.0 | BSD-3-Clause)
```

## License graph (built in or custom)

To decide wether a license is compatible with another a graph of
license compatibilities is used. Bye default License checker uses
licenses as specified FLOSS License Compatibility Graph project. Using
this graph is recommended and if you lack some license we suggest you
get in contact with that project to update their graph. If you still
would like to use your own graph you can use the command line option
```--compatibility-file```.


## Policy (no built in, optional)

With a policy file you can tell this tool which licenses you're not
allowing (denied) and which you preferr not to use (gray). Here's an
example policy file:

```
{
    "meta" : {
        "software":"License Policy Checker",
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

## License later defininitions (built in or custom)

Some licenses can be specifed saying "or-later", e g
GPL-2.0-or-later. You can provide a list of definitions for this tool
to decide how these licenses should be interpreted.

Let's start with a example:


```
{
    "meta": {
        "software":"License Policy Checker",
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

# Output

A report of the component's compliance is created. By default a short text report is created. With the tools also comes a couple of Report format that can be used.

## Report formats

### JSON

This is currently rewritten and not available.

### Markdown (pdf, html ..)

Using this format you can create txt, html, pdf and what format pandoc can create from markdown.

# License of the License Checker

License Checker is released under GPLv3(https://www.gnu.org/licenses/gpl-3.0.en.html)

# Building

## Required tools

* jq

* GNU Make

* JDK (Jav Development Kit) (7 or higher)

* bash

* wget

* Java dependencies will be download when you configure

## Required java components

* GSON (downloaded automatically with configure)

* Apache common (downloaded automatically with configure)

### Install required dependencies

We have setup scripts for some GNU/Linux distributions:

* Debian ```var/setup-linux-debian.sh```

* Fedora: ```var/setup-linux-fedora.sh```

* Ubuntu: ```var/setup-linux-ubuntu.sh```

## Building

### Check tools

~~~
./configure
~~~

### Build

~~~
make
~~~

### Test

~~~
make test
~~~

# The way License Checker works

Check out: (doc/how.md)

# Example

The below checks license checker itself. The component License Checker
and its dependencies are specified in the file
`./meta/license-policy-checker.json` as found in the source code.

~~~
bin/license-checker.sh -v -c ./meta/license-checker.json 

~~~


