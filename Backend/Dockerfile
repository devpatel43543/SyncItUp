# Backend/Dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and install dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the rest of the backend code
COPY . .

# Build the Spring Boot application
RUN mvn package -DskipTests

# Use a lightweight image for running the app
FROM eclipse-temurin:17

WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
