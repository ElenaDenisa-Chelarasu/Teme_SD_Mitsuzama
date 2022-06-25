FROM openjdk:8-jdk-alpine
ADD target/StudentMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar StudentMicroservice.jar
ADD questions_database.txt questions_database.txt
ENTRYPOINT ["java","-jar", "StudentMicroservice.jar"]
