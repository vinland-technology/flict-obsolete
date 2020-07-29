FROM sandklef/foss-license-checker-base

# Update per release
# 
ENV FLICT_RELEASE=0.1-beta12

#
#
#
#
ENV FLICT_NAME=flict
ENV FLICT_DIR=flict
ENV FLICT_BASE_URL=https://gitlab.com/sandklef/flict/-/archive/$FLICT_RELEASE
ENV FLICT_ZIP=$FLICT_NAME-$FLICT_RELEASE.zip
ENV FLICT_BIN=bin/flict

# Fetch FOSS License Compliance Tool from server
ADD  $FLICT_BASE_URL/$FLICT_ZIP .

# Unpack
RUN mkdir $FLICT_DIR && cd $FLICT_DIR && unzip ../$FLICT_ZIP && cd $FLICT_NAME-$FLICT_RELEASE && ./configure --devel && make 

# Set working dir
WORKDIR $FLICT_DIR

# Basic test
ENV LICENSE_DIR="share/licenses/json "
ENV COMPAT_FILE="share/licenses/connections/compatibilities.json "
ENV LATER_FILE="share/licenses/later/later-definitions.json "

ENV LICENSE_ARGS=" --license-dir $LICENSE_DIR"
ENV COMPAT_ARGS=" --compatibility-file $COMPAT_FILE "
ENV LATER_ARGS=" --later-file $LATER_FILE "

CMD cd $FLICT_NAME-$FLICT_RELEASE && bin/check-components.sh 
