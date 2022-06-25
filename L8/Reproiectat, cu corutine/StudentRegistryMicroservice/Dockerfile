FROM openjdk:8-jdk-alpine
ADD target/MessageManagerMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar MessageManagerMicroservice.jar
ENTRYPOINT ["java","-jar", "MessageManagerMicroservice.jar"]