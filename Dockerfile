# Using Java 17 base image
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source
COPY src src

# Build
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Copy jar file
RUN cp build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
