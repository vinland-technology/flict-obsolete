# Using the FOSS License Checker Docker image

# Required tools

docker - see docker.io for information on how to install docker

# Get Foss License Checker docker image

```
docker pull sandklef/foss-license-checker
```

# Prepare to run the FOSS License Checker

Create a directory for the components

```
mkdir components
```

# Run FOSS License Checker

Put the components you want checked in the above created folder (```components```). 

```
docker run -it -v `pwd`/components/:/components sandklef/foss-license-checker 
```

# Example run

We're going to analyse a component called Cairo. The component is
specified in the JSON file ```cairo.json``` and the ```components```
directory.

Let's have a look at the ```components``` directory
```
$ tree --charset=ascii components/
components/
`-- cairo.json

0 directories, 1 file
```

The component itself looks like this:
```
$ cat components/cairo.json 
{
    "meta": {
        "software":"License Policy Checker",
        "version":"0.1"
    },
    "component": {
        "name": "cairo",
        "license": "Apache-2.0",
        "dependencies": [],
        "include_dependencies": [
            "fontconfig",
            "freetype",
            "glibc",
            "libpng",
            "pixman",
            "zlib"            
        ]
    }
}
```

Let's run the check:
```
$ docker run -it -v `pwd`/components/:/components sandklef/foss-license-checker 
FOSS License Checker - for use in docker

Check components:
===========================

  cairo
  -------------------------
   * compliance:   yes
   * convert report to: pdf html docx opendocument plain json 

```

You should be able to see the report in various formats in the ```components/report``` directory.

```
$ tree --charset=ascii components/
components/
|-- cairo.json
|-- check-components.log
`-- reports
    |-- cairo
    |   |-- report-cairo.docx
    |   |-- report-cairo.html
    |   |-- report-cairo.json
    |   |-- report-cairo.md
    |   |-- report-cairo.opendocument
    |   |-- report-cairo.pdf
    |   `-- report-cairo.plain
    `-- summary.log

2 directories, 10 files
```

