FROM eclipse-temurin:21-jdk
WORKDIR /app

ARG JAR_FILE=target/prometheus-api.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8093

CMD ["java", "-jar", "app.jar"]
