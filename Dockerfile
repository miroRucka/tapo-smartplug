# Build docker image running java
# NOTE: make sure to match the JDK version with the one building the jar

# For Mac M1 use:
# FROM arm64v8/openjdk:18

FROM openjdk:18-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
