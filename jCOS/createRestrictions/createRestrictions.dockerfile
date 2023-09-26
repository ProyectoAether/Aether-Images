# Type: Dockerfile
# Description: Dockerfile for the jCOS createRestrictions component

# Create the build image

FROM maven:3.6.3-jdk-11 AS build

LABEL Khaos Research Group <khaos.uma.es>

WORKDIR /usr/local/src/
COPY . /usr/local/src/

# Build the component jar file

RUN  mvn -f /usr/local/src/script/pom.xml clean package

# Create the java image

FROM openjdk:11-jre-slim

# Copy the jar file from the build image to the java image

COPY --from=build /usr/local/src/script/target/jCOS_CreateRestrictions-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/src/jCOS_CreateRestrictions-1.0-SNAPSHOT-jar-with-dependencies.jar

# Set the working directory

WORKDIR /usr/local/src/

# Set the entrypoint

ENTRYPOINT ["java","-jar","jCOS_CreateRestrictions-1.0-SNAPSHOT-jar-with-dependencies.jar"]