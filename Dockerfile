FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

ARG PORT

ENV PORT=${PORT}

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

COPY src src

RUN java -version

RUN ./gradlew clean bootJar --no-daemon -x test


FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build \
    /app/build/libs/*.jar \
    ./maintenance-service-api-gateway.jar

RUN chmod +x ./maintenance-service-api-gateway.jar

EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "./maintenance-service-api-gateway.jar"]