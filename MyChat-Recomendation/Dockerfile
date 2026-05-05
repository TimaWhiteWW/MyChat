FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src /app/src
RUN mvn clean package -DskipTest


FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

COPY --from=build /app/target/MyChat-Recomendation-0.0.1-SNAPSHOT.jar /app/MyChat-Recomendation.jar

EXPOSE ${MAIN_PORT}

ENTRYPOINT ["java", "-jar", "MyChat-Recomendation.jar"]