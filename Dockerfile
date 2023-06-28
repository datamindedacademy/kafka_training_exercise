FROM ubuntu:22.04

# install wget and tar
RUN apt-get update && apt-get install -y wget tar

# install Java 17 and set JAVA_HOME
RUN apt-get install -y openjdk-17-jdk
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64

# install dependencies
RUN mkdir kafka_cli
RUN wget https://archive.apache.org/dist/kafka/3.1.2/kafka_2.13-3.1.2.tgz -P kafka_cli
RUN tar -xzf kafka_cli/kafka_2.13-3.1.2.tgz --directory kafka_cli

# set path
ENV PATH="/kafka_cli/kafka_2.13-3.1.2/bin:$PATH"

# set sleep as entrypoint
ENTRYPOINT ["sleep"]
CMD ["86400"]