# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13

WORKDIR /app

COPY . .

CMD mvn clean install
CMD java -jar ./target/CodeTest-Crosskey-1.0-SNAPSHOT.jar .prospects.txt
