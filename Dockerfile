FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/sol-0.0.1-SNAPSHOT.jar sol.jar
EXPOSE 8080
ENTRYPOINT ["JAVA", "-jar", "sol.jar"]