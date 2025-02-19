# Använd en minimal JDK-bild för att köra applikationen
FROM --platform=linux/amd64 eclipse-temurin:21-jre

# Sätt arbetskatalogen i containern
WORKDIR /app

# Kopiera in den färdigbyggda JAR-filen
COPY target/*.jar app.jar

# Exponera porten
EXPOSE 8080

# Starta Spring Boot-applikationen
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
