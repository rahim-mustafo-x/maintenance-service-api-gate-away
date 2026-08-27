FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew nativeCompile --no-daemon


FROM debian:bookworm-slim

WORKDIR /app

# Non-secret build argument
ARG PORT=8090

# Default runtime value
ENV PORT=${PORT}

COPY --from=builder /app/build/native/nativeCompile/* /app/app

RUN chmod +x /app/app

EXPOSE ${PORT}

ENTRYPOINT ["/app/app"]