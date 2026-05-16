FROM node:22-slim AS css-build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY input.css .
COPY src src
RUN npx @tailwindcss/cli -i ./input.css -o ./src/main/resources/static/css/output.css

FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY --from=css-build /app/src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dwicket.configuration=deployment", "-jar", "app.jar"]