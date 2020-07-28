# Instructions on releasing FOSS License Compatibility Tool

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

Click "New release"(https://gitlab.com/sandklef/foss-license-compatibility-tool/-/tags/new) and fill in the version name above.

### Upload binary release

Upload/attach the newly created binary file (zip) to the release

## Test the release

.. TBD