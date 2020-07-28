FROM sandklef/foss-license-checker-base

#
# 
ENV FLC_RELEASE=0.1-beta10
ENV FLC=foss-license-checker
ENV FLC_DIR=foss-license-checker
ENV FLC_BASE_URL=https://gitlab.com/sandklef/foss-license-checker/-/archive/$FLC_RELEASE
ENV FLC_ZIP=foss-license-checker-$FLC_RELEASE.zip
ENV FLC_BIN=bin/foss-license-checker.sh

# Fetch FOSS License Checker from server
ADD  $FLC_BASE_URL/$FLC_ZIP .

# Unpack
RUN mkdir $FLC_DIR && cd $FLC_DIR && unzip ../$FLC_ZIP && cd foss-license-checker-$FLC_RELEASE && ./configure --devel && make 

# Set working dir
WORKDIR $FLC_DIR

# Basic test
ENV LICENSE_DIR="share/licenses/json "
ENV COMPAT_FILE="share/licenses/connections/foss-license-checker.json "
ENV LATER_FILE="share/licenses/later/later-definitions.json "

ENV LICENSE_ARGS=" --license-dir $LICENSE_DIR"
ENV COMPAT_ARGS=" --compatibility-file $COMPAT_FILE "
ENV LATER_ARGS=" --later-file $LATER_FILE "

CMD cd foss-license-checker-$FLC_RELEASE && bin/check-components.sh 
