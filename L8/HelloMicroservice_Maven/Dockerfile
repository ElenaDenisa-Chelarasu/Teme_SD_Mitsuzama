FROM openjdk:8-jdk-alpine
ADD target/HelloMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar HelloMicroservice.jar

ENTRYPOINT ["java","-jar", "HelloMicroservice.jar"]