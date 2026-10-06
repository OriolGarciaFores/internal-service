FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Europe/Madrid"

COPY --from=builder /app/target/internal-service-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]