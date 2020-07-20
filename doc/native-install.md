# Native installation

## Required tools

* jq

* GNU Make

* JDK (Jav Development Kit) (7 or higher)

* bash

* wget

* Extra Java components will be download when you configure

* pdfunite

## Required java components

* GSON (downloaded automatically with configure)

* Apache common (downloaded automatically with configure)

### Install required tools

~~~
./var/setup.sh
~~~

*Note: we're currently only supporting Debian, Ubuntu, Fedroa, Redhat GNU/Linux. If you're usong something else, please check the required tools above* 


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

## Example

The below checks license checker itself. The component License Checker
and its dependencies are specified in the file
`./meta/license-policy-checker.json` as found in the source code.

~~~
foss-license-checker.sh -c ./meta/foss-license-checker.json 
~~~


