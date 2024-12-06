# Run stage
FROM eclipse-temurin:17-jre-alpine

RUN mkdir -p /app/fonts
COPY src/main/resources/fonts/NanumGothic-Regular.ttf /app/fonts/


WORKDIR /app
COPY  build/libs/*.jar app.jar

EXPOSE 6060
ENTRYPOINT ["java", "-Xms256m", "-Xmx2048m", "-jar", "app.jar"]