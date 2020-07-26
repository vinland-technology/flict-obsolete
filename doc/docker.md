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
docker run -it -v `pwd`/components/:/components --name sandklef-flc sandklef/foss-license-checker 
```
