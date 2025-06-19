FROM eclipse-temurin:21-jdk
WORKDIR /app

ARG JAR_FILE=target/otel-collector-api.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8095

CMD ["java", "-jar", "app.jar"]
