# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    chmod +x ./mvnw && ./mvnw dependency:go-offline -q

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser
EXPOSE 8084

# validate endpoint trả về 200 ngay cả khi voucher không tồn tại
HEALTHCHECK --interval=15s --timeout=10s --start-period=60s --retries=5 \
    CMD wget -qO- "http://localhost:8084/api/vouchers/validate?code=health&orderTotal=0" || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
