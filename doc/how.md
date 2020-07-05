# How does License Checker work

We can split the functionality of this tool in to two main parts:

* parsing and simplifying the licenses

* checking if a combination is compliant

## Parse and simplify licenses

Let's look at a simple license expression: *MIT*. This is easy to
understand. But when having license expressions such as
*GPL-2.0-or-later | ( Apache-2.0 & MIT)* things become o bit
trickier. Since there may be bugs in our tools we've decided to go for
an approach that make it easy to track down bugs by doing one thing at
a time and be able to check the intermediate states.

### Expand later expressions

A license expression such as *MIT* is left unchanged in this
phase. But a license expression ike "GPL-2.0-or-later" is expanded
according to rules specified in a separate file. This tool comes with
a file that specifies some later definitions. You can specify your own
such file, but we assume most users will use our so let's assume
you're doing that. *GPL-2.0-or-later* will be expanded to
*(GPL-2.0-or-later | GPL-3.0-only)*, so in short:

```
GPL-2.0-or-later => (GPL-2.0-or-later | GPL-3.0-only)
```

### License expressions with parenthesises 

### Polish notation

### List of AND expressions

## Check compliance

We've been trying out a lot of different approaches, such as trying to
implement rules per obligation, but have for now ended up using a
graph of dependencies.
