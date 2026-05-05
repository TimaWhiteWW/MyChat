# Этап сборки: берем образ с Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src /app/src
RUN mvn clean package -DskipTests

# Этап выполнения: Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app


# Копируем только собранный JAR
COPY --from=build /app/target/MyChat_UserMicroservice-0.0.1-SNAPSHOT.jar /app/MyChat-UserMicroservice.jar


EXPOSE ${MAIN_PORT}

ENTRYPOINT ["java", "-jar", "MyChat-UserMicroservice.jar"]
