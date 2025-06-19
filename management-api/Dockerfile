FROM eclipse-temurin:21-jdk
WORKDIR /app

ARG JAR_FILE=target/management-api.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8092

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
