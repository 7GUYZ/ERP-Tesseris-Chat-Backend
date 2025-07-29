# Build stage
FROM amazoncorretto:17-alpine-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

# Run stage
FROM amazoncorretto:17-alpine-jdk

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 19092

ARG PROFILE_ACTIVE
ENV SPRING_PROFILES_ACTIVE=${PROFILE_ACTIVE}

ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/app.jar"] 