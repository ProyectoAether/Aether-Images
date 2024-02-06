# Type: Dockerfile
# Description: Dockerfile for the jCOS solveWithFABIOLA component

# Create the build image

FROM maven:3.6.3-jdk-11 AS build

LABEL Khaos Research Group <khaos.uma.es>

# Set the working directory

WORKDIR /usr/local/src/

# Copy the pom.xml file to the working directory

COPY ./script/pom.xml .

# Download the dependencies

RUN mvn dependency:go-offline -B

# Copy source code to the working directory

COPY ./script/src ./src

# Copy input files to the input directory

COPY ./input ./script/input

# Build the component jar file

RUN  mvn package

# Create the java image

FROM openjdk:11-jre-slim as app

WORKDIR /usr/local/src/

# Copy the jar file from the build image to the java image

COPY --from=build /usr/local/src/target/fabiola-0.6.0-BETA-WG.0.2-jar-with-dependencies.jar .

# Set the working directory

COPY . /usr/local/src/

# Set the entrypoint

ENTRYPOINT ["java","-jar","fabiola-0.6.0-BETA-WG.0.2-jar-with-dependencies.jar"]