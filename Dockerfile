FROM ubuntu:20.04 as ar4k-builder
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y openjdk-11-jdk
COPY . /ar4kAgent
WORKDIR /ar4kAgent
RUN chmod +x gradlew
RUN ./gradlew clean :opcuad:distTar -x test -Dorg.gradle.jvmargs="-Xms512M -Xmx4G" --info

FROM ubuntu:20.04
ARG MAINTAINER="Andrea Ambrosini <andrea.ambrosini@rossonet.org>"
RUN apt update && \
	DEBIAN_FRONTEND=noninteractive apt install -y openjdk-11-jre tar wget && \
	apt-get clean && rm -rf /var/lib/apt/lists/*
ENTRYPOINT ["/opcuad/bin/opcuad"]
RUN mkdir -p /neo4j-engine/neo4j-community && cd /neo4j-engine/neo4j-community && \
	wget https://www.rossonet.net/dati/neo4j/neo4j-community-4.4.13-unix.tar.gz && \
	tar -xzf neo4j-community-4.4.13-unix.tar.gz && chmod +x neo4j-community-4.4.13/bin/neo4j && \
	chmod +x neo4j-community-4.4.13/bin/neo4j-admin && rm neo4j-community-4.4.13-unix.tar.gz
COPY --from=ar4k-builder /ar4kAgent/opcuad/build/distributions/opcuad-[0-9]*.tar /opcuad.tar
RUN tar -xf opcuad.tar && mv opcuad-* opcuad && rm opcuad.tar

#ENTRYPOINT ["java"]
#CMD ["-Djava.net.preferIPv4Stack=true","-XshowSettings:vm","-Djava.security.egd=file:/dev/./urandom","-jar","/opcuad.jar"]
