FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copy build files
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Download dependencies
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build application
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 3002

ENTRYPOINT ["java", "-jar", "app.jar"]
