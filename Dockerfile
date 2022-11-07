FROM rockylinux:latest as ar4k-builder
RUN yum install -y java-1.8.0-openjdk-devel
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0
COPY . /ar4kAgent
WORKDIR /ar4kAgent
RUN chmod +x gradlew
RUN ./gradlew clean shadowJar -Dorg.gradle.jvmargs="-Xms512M -Xmx4G" --info

FROM rockylinux:latest
RUN yum install -y java-1.8.0-openjdk wget && yum update -y && yum clean all
COPY --from=ar4k-builder /ar4kAgent/build/libs/*-all.jar /agent.jar
ENTRYPOINT ["java"]
CMD ["-XX:+UnlockExperimentalVMOptions","-Djava.net.preferIPv4Stack=true","-XX:+UseCGroupMemoryLimitForHeap","-XshowSettings:vm","-Djava.security.egd=file:/dev/./urandom","-jar","/agent.jar"]
