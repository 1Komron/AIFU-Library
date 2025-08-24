FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/migration-service-0.0.1-SNAPSHOT.jar app-migration.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app-migration.jar"]
