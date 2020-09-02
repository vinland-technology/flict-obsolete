<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# How does FOSS License Compliance Tool work

We can split the functionality of this tool in to two main parts:

* parsing and simplifying the licenses

* combine component and dependencies into a list of all possible combinations

* checking if a combination is compliant

## Parse and simplify licenses

Let's look at a simple license expression: *MIT*. This is easy to
understand. But when having license expressions such as
*GPL-2.0-or-later | ( Apache-2.0 & MIT)* things become o bit
trickier. Since there may be bugs in our tools we've decided to go for
an approach that make it easy to track down bugs by doing one thing at
a time and be able to check the intermediate states.

The idea is to create a list of all possible license combinations for
a component and its dependencies. Each of the licenses should be a
simple expression (with only AND operation allowed). Example:

```
   Program (GPL-2.0-only)
        |
  +-----+-------------+
  |                   |
  lib1 (MIT&Zlib)   lib2 (Apache-2.0 | LGPL-2.1-only)
```

Should be translated to two different combinations of the same component:

```
   Program (GPL-2.0-only)
        |
  +-----+-------------+
  |                   |
  lib1 (MIT&Zlib)   lib2 (LGPL-2.1-only)
```

and 

```
   Program (GPL-2.0-only)
        |
  +-----+-------------+
  |                   |
  lib1 (MIT&Zlib)   lib2 (Apache-2.0)
```

These two are easier to check for compliance than using complex
license expressions. This means that we may end up with a list of the
same component with different license combinations but at least these
are easy to 1) debug and 2) check for compliance.

So, we're trying to make our initial problem of a component with
dependencies and complex license expression to a more sequential
problem of a list of a component with dependencies all using simple
license expressions.

### Expand later expressions

A license expression such as *MIT* is left unchanged in this
phase. But a license expression ike "GPL-2.0-or-later" is expanded
according to rules specified in a separate file. This tool comes with
a file that specifies some later definitions. You can specify your own
such file, but we assume most users will use our so let's assume
you're doing that. *GPL-2.0-or-later* will be expanded to
*(GPL-2.0-or-later | GPL-3.0-only)*. Example:

```
  GPL-2.0-or-later

  =>

  (GPL-2.0-or-later | GPL-3.0-only)
```

### License expressions with parenthesises 

All licenses that are have an AND operator apllied to them are grouped together using parenthesises. A license expression like *MIT & Apache-2.0 | GPL-2.0-only* is in this phase transformed into  *(MIT & Apache-2.0) | GPL-2.0-only*. Example:

```
  MIT & Apache-2.0 | GPL-2.0-only
 
  =>

  (MIT & Apache-2.0) | GPL-2.0-only
```

### Polish notation

In this phase a license expression is turned in to an algebraic expression with polish notaion using *AND* and *OR* and lists. The expression *MIT | BSD-3-Clause* will be trans formed into *OR [MIT, BSD-3-Clause]*. Example:

```
  (MIT & Apache-2.0) | GPL-2.0-only

  =>

  OR[AND["MIT", "Apache-2.0"]  , "GPL-2.0-only"]
```

### List of AND expressions

The last round we're taking the OR:ed lists and split them into separate lists. The expression ** will become two list, see example:

```
  OR[AND["MIT", "Apache-2.0"]  , "GPL-2.0-only"]

  => 

 [
   [MIT, Apache-2.0]
   [GPL-2.0-only]
 ]
```

All the lists (in the list) are license combinations for the
component's license expression. So the possible license combination of
this components are *MIT & Apache-2.0* or *GPL-2.0-only*, wihch really
is where we started. But we have the licenses in a data structure (a
list of list), which is nice.

### Debugging the transformation

If you would like to see how this tool is tranforming your license expression before using it in later phases you try using the option ```--expression```.

To test the expression ```MIT & Apache-2.0 | GPL-2.0-only```, do:

```
flict -e "MIT & Apache-2.0 | GPL-2.0-only"

```

## Combine components and dependencies in to a list

If we're not having and dependencies for a component we really have an
easy task of checking compliance. So let's assume we have two
dependency components. Each component has their own list of possible
license combinations. Our approach to this is to create a list of our
top level component with all different license combination. Let's look at the earlier example:

```
   Program (GPL-2.0-only)
        |
  +-----+-------------+
  |                   |
  lib1 (MIT&Zlib)   lib2 (Apache-2.0 | LGPL-2.1-only)
```

If we work through the license expressions first we'll end up with:

```
   Program [ [GPL-2.0-only] ]
        |
  +-----+-------------+
  |                   |
  lib1 [ [MIT&Zlib] ]   lib2 [ [Apache-2.0], [LGPL-2.1-only] ]
```

and then if we create one top component (Program) as we the product of all list sizes. In our case the list sizes are 1, 1 and 2 so the product is 2. This means we will have two components (Program) with licenses as this:

```
   Program (GPL-2.0-only)
        |
  +-----+-------------+
  |                   |
  lib1 (MIT&Zlib)   lib2 (LGPL-2.1-only)
```

and 

```
   Program (GPL-2.0-only)
        |
  +-----+-------------+
  |                   |
  lib1 (MIT&Zlib)   lib2 (Apache-2.0)
```

## Check compliance

### License compliance

We've been trying out a lot of different approaches, such as trying to
implement rules per obligation, but have for now ended up using a
graph of dependencies.

Now, we have a couple of otp components with different license
combinations. The trick with creaeting one component "copy" per
license combination is to have a couple (2 in this case) simple
license situations. It is now fairly easy to go through the component
from the top and check the dependency components if they're compliant.

In th example above we will check for compliance:

* the first component: *GPL-2.0-only* is compliant with both *MIT* and *Zlib*

* the second component: *GPL-2.0-only* is compliant with *Apache-2.0*

We save the compliance state for each component.

The compliance between two (or more) licenses is done using the
graph. If there is a way from a license to another in the graph then
they're compliant. In our example we can go for the first component
copy:

* can *GPL-2.0-only* use *MIT*

* can *GPL-2.0-only* use *Zlib*

If both of them are true, then the component, or rather the component with this license combination, is compliant.

### Policy

If the user provides a policy we walk through the list of component
copies to see if any of the licenses in the lists are marked as
*denied* or *gray*. If they are, this is stored together with the
specific component.