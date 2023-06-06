FROM ghcr.io/graalvm/native-image:ol8-java11-22.3.2

LABEL maintainer="devatherock@gmail.com"
LABEL io.github.devatherock.version="2.0.0"

RUN microdnf -y install xz \
	&& microdnf clean all \
	&& curl --location --output upx-3.96-amd64_linux.tar.xz "https://github.com/upx/upx/releases/download/v3.96/upx-3.96-amd64_linux.tar.xz" \
	&& tar -xJf upx-3.96-amd64_linux.tar.xz \
	&& cp upx-3.96-amd64_linux/upx /bin/

COPY entry-point.sh /scripts/entry-point.sh
COPY CreateNativeImage.jar /scripts/CreateNativeImage.jar

ENTRYPOINT ["/bin/sh", "/scripts/entry-point.sh"]