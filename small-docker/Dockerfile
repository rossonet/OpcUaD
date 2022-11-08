FROM ubuntu:20.04 as ar4k-builder
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y openjdk-11-jdk
COPY ./.. /ar4kAgent
WORKDIR /ar4kAgent
RUN ls && chmod +x gradlew
RUN ./gradlew clean :opcuad:distTar -x test -Dorg.gradle.jvmargs="-Xms512M -Xmx4G" --info

FROM ubuntu:20.04
ARG MAINTAINER="Andrea Ambrosini <andrea.ambrosini@rossonet.org>"
RUN apt update && \
	DEBIAN_FRONTEND=noninteractive apt install -y openjdk-11-jre tar && \
	apt-get clean && rm -rf /var/lib/apt/lists/*
ENTRYPOINT ["/opcuad/bin/opcuad"]
COPY --from=ar4k-builder /ar4kAgent/opcuad/build/distributions/opcuad-[0-9]*.tar /opcuad.tar
RUN tar -xf opcuad.tar && mv opcuad-* opcuad && rm opcuad.tar

#ENTRYPOINT ["java"]
#CMD ["-Djava.net.preferIPv4Stack=true","-XshowSettings:vm","-Djava.security.egd=file:/dev/./urandom","-jar","/opcuad.jar"]
