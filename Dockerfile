FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /app

COPY . .

RUN ./mvnw clean native:compile -Pnative -DskipTests


FROM debian:bookworm-slim

WORKDIR /app

# Non-secret build argument
ARG PORT=8090

# Default runtime value
ENV PORT=${PORT}

COPY --from=builder /app/target/* /app/app

RUN chmod +x /app/app

EXPOSE ${PORT}

ENTRYPOINT ["/app/app"]