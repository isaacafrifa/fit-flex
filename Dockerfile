# Build stage
FROM maven:3.8.7-openjdk-18 AS builder

# Define build arguments for JAR name pattern
# Default to '1.0.0' if APP_VERSION is not provided
ARG VERSION=${APP_VERSION:-1.0.0}
ARG JAR_FILE=target/${VERSION}.jar

# Set working directory for the application
WORKDIR /app

# Copy project code to the build context
COPY . .

# Build the application using Maven
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine3.19

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/target/${JAR_FILE} ${JAR_FILE}

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "target/${JAR_FILE}"]
