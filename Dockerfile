FROM oracle/graalvm-ce:20.1.0-java11

LABEL maintainer="devatherock@gmail.com"
LABEL io.github.devatherock.version="0.1.0"

RUN gu install native-image

COPY entry-point.sh /scripts/entry-point.sh
COPY CreateNativeImage.jar /scripts/CreateNativeImage.jar

ENTRYPOINT ["/bin/sh", "/scripts/entry-point.sh"]