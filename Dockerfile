# Build Stage
FROM maven:3.9.0-eclipse-temurin-19 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/sol-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]