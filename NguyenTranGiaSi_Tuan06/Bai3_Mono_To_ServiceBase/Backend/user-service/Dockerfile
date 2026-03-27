# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Cache Maven dependencies separately from source → only re-downloads when pom.xml changes
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    chmod +x ./mvnw && ./mvnw dependency:go-offline -q

# Build source (cache hit on deps layer above if only src changes)
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -q

# ── Runtime image (JRE only, không có JDK) ──────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Non-root user cho bảo mật
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser
EXPOSE 8081

HEALTHCHECK --interval=15s --timeout=10s --start-period=60s --retries=5 \
    CMD wget -qO- http://localhost:8081/api/users || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
