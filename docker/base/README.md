<!--
SPDX-FileCopyrightText: 2020 Henrik Sandklef <hesa@sandklef.com>

SPDX-License-Identifier: GPL-3.0-or-later
-->

# Information for developers

# Basic

```
IMAGE_NAME=sandklef/foss-license-checker-base
```

# build 

```
docker build $(pwd) -t $IMAGE_NAME
```

# Clean containers

```
for i in $(docker ps -a| grep -v CONTAINER | awk '{ print $1 }' ); do docker container rm $i; done
for i in $(docker container ls -a | grep $CONTAINER_NAME | awk '{ print $1}' ); do docker container rm $i; done
```

# Clean image

```
docker image rm $IMAGE_NAME
```


# Upload

```
docker push sandklef/foss-license-checker-base:latest
```