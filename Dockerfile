# Build stage
FROM maven:3.8.7-openjdk-18 AS builder

# Set working directory for the application
WORKDIR /build

# Copy project code to the build context
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src

# Build the application using Maven
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine3.19

# Set the working directory
WORKDIR /app

# Set the environment variable
ENV POSTGRES_URL=postgres_url
ENV POSTGRES_USERNAME=postgres_username
ENV POSTGRES_PASSWORD=postgres_password

# Copy the JAR file from the build stage
COPY --from=builder /build/target/*.jar /app/app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
