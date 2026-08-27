FROM gradle:9.5.1-jdk25 AS build

WORKDIR /app

ARG PORT

ENV PORT=${PORT}

COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN java -version

RUN gradle clean bootJar --no-daemon -x test -x processAot


FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build \
    /app/build/libs/*.jar \
    ./maintenance-service-api-gateway.jar

RUN chmod +x ./maintenance-service-api-gateway.jar

EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "./maintenance-service-api-gateway.jar"]