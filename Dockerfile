# Use Java 17 (adjust if you're using a different Java version)
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the JAR file (adjust the filename to match your actual JAR file name)
COPY target/error-code-manager-0.0.1-SNAPSHOT.jar app.jar

# Environment variables (optional)
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
