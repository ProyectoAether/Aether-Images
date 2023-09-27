# Type: Dockerfile
# Description: Dockerfile for the jCOS solveWithJMetal component

# Create the build image

FROM maven:3.6.3-jdk-11 AS build

LABEL Khaos Research Group <khaos.uma.es>

# Set the working directory

WORKDIR /usr/local/src/

# Copy the pom.xml file to the working directory

COPY ./script/pom.xml .

# Download the dependencies

RUN mvn dependency:go-offline -B

# Build the component jar file

COPY ./script/src ./src
RUN  mvn package

# Create the java image

FROM openjdk:11-jre-slim as app

WORKDIR /usr/local/src/

# Copy the jar file from the build image to the java image

COPY --from=build /usr/local/src/target/jCOS_SolveWithJMetal-1.0-SNAPSHOT-jar-with-dependencies.jar .

# Set the working directory

COPY . /usr/local/src/

# Set the entrypoint

ENTRYPOINT ["java","-jar","jCOS_SolveWithJMetal-1.0-SNAPSHOT-jar-with-dependencies.jar"]