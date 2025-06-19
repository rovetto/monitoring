FROM eclipse-temurin:21-jdk
WORKDIR /app

ARG JAR_FILE=target/grafana-api.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8094

CMD ["java", "-jar", "app.jar"]
