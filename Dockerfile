# Use OpenJDK 24 slim image
FROM openjdk:24-slim

# Set working directory
WORKDIR /app

# Set the JAR file location (flexible way)
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
