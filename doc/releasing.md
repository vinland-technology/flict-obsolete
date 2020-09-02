<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# Instructions on releasing FOSS License Compliance Tool

## Commit, clean up ...

... and when done, push to gitlab (primary branch)

## Build and test

```
./configure --devel && make clean all test dist test-dist
```

## Name the release

Define the version and update the version in the following files:

* com/sandklef/compliance/utils/Version.java

* docker/foss-license-checker/Dockerfile

## Verify and create release

```
./var/release.sh VERSION-NUMBER
```

*Note: this script pushes an image over at docker.io*

## Create new release at gitlab

Click "New release"(https://gitlab.com/sandklef/flict/-/tags/new) and fill in the version name above.

### Upload binary release

Upload/attach the newly created binary file (zip) to the release

## Test the release

.. TBD