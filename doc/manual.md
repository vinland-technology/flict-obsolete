# License Checker User's Guide

# Introduction

License Checker is an Free and Open Source Software compliance tool
aimed at detecting license violations either with or without a
policy. It assumes every component has a known license expression.

License Checker can also conclude outbound licenses for your component.

# Invoking License Checker

```
license-checker.sh [OPTIONS]
```

## Options

### --pdf

### -- compatibility-file, -cf [FILE]

Which compatibility graph to use. Default file is ```licenses/connections/license-checker.json``` as found in the installtion directory.

### --component-file, -c [FILE]

Which component to analyse. No default files.

### --expression, -e [EXPR]

Parse show result of a license expression. The expression is supplied on the command line.

### --violation, -v 

Check for license compatibility and violations. This is the default mode.

### ---policy-file, -p [FILE]

Set the policy file to use. A policy is optional, without a policy all licenses are considered equal.

### --license-dir, -ld

Set the directory containing the license definitions. Default directory is ```licenses/json/``` as found in the installtion directory.

### --later-file, -lf

Set the file containing the definitions of how to interpret licenses with "later" definitions (e g *GPL-2.0-or-later*). Default file is ```licenses/later/later-definitions.json``` as found in the installtion directory.

### --output, -o [FILE]

Set the output file. Default is stdout.

### --connection-graph, -cg

Output the compatibility graph in dot format.

### --json, -j

Output the report in JSON format.

### --markdown, -md

Output the report in Markdown format.

### --pdf

Output the report in pdf format.

### --print-licenses, -pl

Print the licenses supported by License Checker.

### --help, -h

Print a simple help message.

### --debug, -d

Enable debug messages. This is intended for our develoeprs.

### --debug-class, -dc

Set which class to debug. This is intended for our develoeprs.

### --debug-component-licenses, -dcl

Outpout the intermediate stages when transforming a component to the internal structure. This is intended for our develoeprs.


