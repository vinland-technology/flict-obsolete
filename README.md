<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# License Checker

License Checker is an Free and Open Source Software compliance tool
aimed at detecting license violations either with or without a
policy. It assumes every component has a known license state.

License Checker can also conclude outbound licenses for your component.

# License of the License Checker

License Checker is released under GPLv3(https://www.gnu.org/licenses/gpl-3.0.en.html)

# Building

## Dependencies

* jq

* GNU Make

* JDK (Jav Development Kit) (7 or higher)

* bash

* wget

* Java dependencies will be download when you configure


## Preparing for build

~~~
./configure
make
make test
~~~

# Example

The below checks license checker itself. The component License Checker
and its dependencies are specified in the file
`./meta/license-policy-checker.json` as found in the source code.

~~~
bin/license-checker.sh -v -c ./meta/license-policy-checker.json 

~~~


