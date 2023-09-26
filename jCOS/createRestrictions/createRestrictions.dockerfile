FROM openjdk:11

LABEL Khaos Research Group <khaos.uma.es>

WORKDIR /usr/local/src/
COPY . /usr/local/src/

# RUN ls -la /usr/local/src/input

ENTRYPOINT ["java","-jar","jCOS_CreateRestrictions-1.0-SNAPSHOT-jar-with-dependencies.jar"]