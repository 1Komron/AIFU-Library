FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/library-bot-0.0.1-SNAPSHOT.jar app-bot.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app-bot.jar"]
