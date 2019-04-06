FROM maven:3-jdk-8 as builder

RUN mkdir /app
ADD . /app
WORKDIR /app
RUN mvn clean install

FROM storm

RUN mkdir -p /app
COPY --from=builder /app/target/phone-discovery-topology-1.0-SNAPSHOT.jar /app/topology.jar
CMD storm jar /app/topology.jar com.hugopicado.examples.storm.PhoneNumberDiscoveryTopology submit
