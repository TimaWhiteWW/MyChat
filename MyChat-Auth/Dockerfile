# Stage 1: Сборка JAR с Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app
COPY . .

RUN mvn clean package

# Stage 2: Запуск в лёгком JRE-окружении
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Открываем стандартный порт Spring Boot (можно изменить)
EXPOSE ${MAIN_PORT}

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
