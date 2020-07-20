# Using the FOSS License Checker Docker image

## Install the Docker image

```
docker pull sandklef/foss-license-checker
```

## Use the docker image

```
docker run -it -v `pwd`/components/:/components --name flc sandklef/foss-license-checker
```
