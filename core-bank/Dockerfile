# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Xms256m", "-Xmx2048m", "-jar", "app.jar"]