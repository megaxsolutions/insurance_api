FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY notification-service/pom.xml ./notification-service/
RUN mvn -B dependency:resolve

COPY notification-service/src ./notification-service/src
RUN cd notification-service && mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/notification-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
