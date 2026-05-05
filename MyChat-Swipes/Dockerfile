FROM maven:3.9-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src /app/src
RUN mvn clean package -DskipTest


FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/target/MyChat-Swipe-0.0.1-SNAPSHOT.jar /app/MyChat-Swipe.jar

EXPOSE ${MAIN_PORT}

ENTRYPOINT ["java", "-jar", "MyChat-Swipe.jar"]