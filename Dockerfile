# Fetching latest version of Java
FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar

# Copy the jar file into our app
COPY ./target/bookhup-0.0.1-SNAPSHOT.jar app.jar

# Exposing port 8080
EXPOSE 8080

# Starting the application
CMD ["java", "-jar", "/app.jar"]
