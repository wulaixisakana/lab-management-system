# Build stage
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Run stage
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE ${PORT:-8081}

ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT:-8081}", "app.jar"]
