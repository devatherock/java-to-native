FROM devatherock/graalvm:ol8-java11-22.3.2-1

LABEL maintainer="devatherock@gmail.com"
LABEL io.github.devatherock.version="2.1.0"

COPY entry-point.sh /scripts/entry-point.sh
COPY CreateNativeImage.jar /scripts/CreateNativeImage.jar

ENTRYPOINT ["/bin/sh", "/scripts/entry-point.sh"]