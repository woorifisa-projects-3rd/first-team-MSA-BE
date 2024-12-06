# Run stage
FROM eclipse-temurin:17-jre-alpine

RUN mkdir -p /app/fonts
COPY src/main/resources/fonts/NanumGothic-Regular.ttf /app/fonts/

WORKDIR /app
COPY  build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xms256m", "-Xmx1024m", "-jar", "app.jar"]