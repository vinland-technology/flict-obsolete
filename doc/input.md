# Input

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

## Licenses (built in or custom)

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
