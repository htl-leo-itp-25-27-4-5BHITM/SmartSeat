FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /work
COPY --from=build /app/target/*-runner.jar /work/app.jar

EXPOSE 8080
CMD ["java","-jar","/work/app.jar"]