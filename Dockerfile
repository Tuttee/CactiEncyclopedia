FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/CactiEncyclopedia-*.jar app.jar

# Expose the port your application runs on
#EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
