# Use the Debian-based OpenJDK 17 image
FROM eclipse-temurin:17-jre

# Install necessary packages
RUN apt-get update && apt-get install -y \
    openssl \
    iproute2 \
    iputils-ping \
    coreutils \
    curl \
    bash && \
    rm -rf /var/lib/apt/lists/*

# Create the app directory
RUN mkdir -p /app

# Create a non-root user and assign permissions
RUN groupadd -g 901 service_user && \
    useradd -u 901 -g service_user -m service_user && \
    chown -R service_user:service_user /app

# Switch to non-root user for security
USER service_user

# Argument for JAR file
ARG JAR_FILE=transaction-management-system-app/target/*.jar

# Copy the application JAR file to the container
COPY ${JAR_FILE} /app/transaction-management-system.jar

# Copy your specific YAML configuration file to the container
COPY transaction-management-system-app/src/main/resources/application-dev.yml /app/application.yml

# Switch back to root to adjust permissions
USER root
RUN chmod -R +x /app && \
    chown service_user:service_user /app/transaction-management-system.jar /app/application.yml

# Switch back to the non-root user for final runtime
USER service_user

# Expose the application port (8080 by default)
EXPOSE 8080

# Command to run the application with the specific configuration file
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/transaction-management-system.jar", "--spring.config.location=/app/application.yml"]

# Healthcheck to ensure the app is running
HEALTHCHECK --interval=10s --timeout=8s --start-period=120s --retries=3 CMD curl --fail http://localhost:8080/actuator/health || exit 1
