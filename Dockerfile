# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем собранный jar-файл
COPY --from=builder /app/target/*.jar app.jar

# Переменные окружения (значения по умолчанию, могут быть переопределены через docker-compose)
ENV DB_URL=jdbc:postgresql://db:5432/yourdb
ENV DB_USER=postgres
ENV DB_PASSWORD=password

ENV BOT_TOKEN=changeme

ENTRYPOINT ["java", "-jar", "app.jar"]