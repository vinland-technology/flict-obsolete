# FOSS License Checker User's Guide

# Introduction

FOSS License Checker is an Free and Open Source Software compliance tool
aimed at detecting license violations either with or without a
policy. It assumes every component has a known license expression.

FOSS License Checker can also conclude outbound licenses for your component.

# Invoking FOSS License Checker

```
foss-license-checker.sh [OPTIONS]
```

## Options

### ```--compatibility-file, -cf [FILE]```

Which compatibility graph to use. Default file is ```share/licenses/connections/foss-license-checker.json``` as found in the installtion directory.

### ```--component-file, -c [FILE]```

Which component to analyse. No default files.

### ```--expression, -e [EXPR]```

Parse show result of a license expression. The expression is supplied on the command line.

### ```--check-compatibility, -cc ```

Check for license compatibility and violations. This is the default mode.

### ```---policy-file, -p [FILE]```

Set the policy file to use. A policy is optional, without a policy all licenses are considered equal.

### ```--license-dir, -ld```

Set the directory containing the license definitions. Default directory is ```licenses/json/``` as found in the installtion directory.

### ```--later-file, -lf```

Set the file containing the definitions of how to interpret licenses with "later" definitions (e g *GPL-2.0-or-later*). Default file is ```share/licenses/later/later-definitions.json``` as found in the installtion directory.

### ```--output, -o [FILE]```

Set the output file. Default is stdout.

### ```--connection-graph, -cg```

Output the compatibility graph in dot format.

### ```--json, -j```

Output the report in JSON format.

### ```--markdown, -md```

Output the report in Markdown format.

### ```--pdf```

Output the report in pdf format.

### ```--print-licenses, -pl```

Print the licenses supported by FOSS License Checker.

### ```--help, -h```

Print a simple help message.

### ```--version, -v```

Print version number and (c) information.

### ```--debug, -d```

Enable debug messages. This is intended for our developers.

### ```--debug-class, -dc```

Set which class to debug. This is intended for our developers.

### ```--debug-component-licenses, -dcl```

Outpout the intermediate stages when transforming a component to the internal structure. This is intended for our developers.

# Trying the tool out

Let's start of by creating a small component that we can play around with. Save the following data (JSON) in a file called ```awesomeprogram.json```:

```
```

*Note: this file can be found in the source code: 

# Examples

If you want to check a component (as specified in the file ``awesomeprogram.json```), then you'd type:

```
$ foss-license-checker.sh -c awesomeprogram.json

```



