# ========= BUILD STAGE =========
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependency
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# ========= RUNTIME STAGE =========
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Render dùng biến môi trường PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
