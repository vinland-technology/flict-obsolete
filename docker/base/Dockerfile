FROM openjdk:8

#
# 
ENV FLC_RELEASE=
ENV FLC=foss-license-checker
ENV FLC_DIR=foss-license-checker
ENV FLC_BASE_URL=http://kasern.sandklef.com/flc/
ENV FLC_ZIP=foss-license-checker.zip
ENV FLC_BIN=bin/foss-license-checker.sh

# Fetch FOSS License Checker from server
ADD  $FLC_BASE_URL/$FLC_ZIP .

# Unpack
RUN mkdir $FLC_DIR && cd $FLC_DIR && unzip ../$FLC_ZIP 

# Set working dir
WORKDIR $FLC_DIR

# Basic test
ENV LICENSE_DIR="share/licenses/json "
ENV COMPAT_FILE="share/licenses/connections/foss-license-checker.json "
ENV LATER_FILE="share/licenses/later/later-definitions.json "

ENV LICENSE_ARGS=" --license-dir $LICENSE_DIR"
ENV COMPAT_ARGS=" --compatibility-file $COMPAT_FILE "
ENV LATER_ARGS=" --later-file $LATER_FILE "

#RUN ls -al $FLC_BIN $LICENSE_DIR $COMPAT_FILE $LATER_FILE share/components/simple-dep-dual.json
#RUN $FLC_BIN $LICENSE_ARGS $COMPAT_ARGS $LATER_ARGS --version
#RUN apt-get update && apt-get install -y pandoc poppler-utils jq wget make curl texlive-latex-recommended pandoc texlive-latex-base texlive-fonts-recommended texlive-extra-utils texlive-latex-extra
RUN $FLC_BIN $LICENSE_ARGS $COMPAT_ARGS $LATER_ARGS --version && printf "\n\nDocker image works fine\n\n" || printf "\n\n***ERROR*** Docker image does not seem to work\n\n"
#RUN find .

CMD bash share/check-components.sh 


