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

We're going to analyse a component called Super Awesome Program. The component is
specified in the JSON file ```example.json``` and the ```components```
directory.

Let's have a look at the ```components``` directory
```
$ tree --charset=ascii components/
components/
`-- example.json

0 directories, 1 file
```

The component itself looks like this:
```
$ cat components/example.json 
{
    "meta": {
        "software":"License Policy Checker",
        "version" : "0.1"
    },
    "component": 
    { "name":    "Super Awesome Program",
      "license": "GPL-2.0-or-later", 
      "dependencies": [
          { "name":    "Great library",
            "license": "MIT|BSD-3-Clause&GPL-2.0-only ", 
            "dependencies": [  ] 
          } 
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

  example
  -------------------------
   * compliance:   yes
   * convert report to: pdf html docx opendocument plain json 

```

You should be able to see the report in various formats in the ```components/report``` directory.

```
$ tree components/
components/
├── check-components.log
├── example.json
└── reports
    ├── example
    │   ├── report-example.docx
    │   ├── report-example.html
    │   ├── report-example.json
    │   ├── report-example.md
    │   ├── report-example.opendocument
    │   ├── report-example.pdf
    │   └── report-example.plain
    └── summary.log

2 directories, 10 files
```

