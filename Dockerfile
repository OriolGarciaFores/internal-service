FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Europe/Madrid"

COPY target/internal-service-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]