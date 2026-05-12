FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY service-registry/pom.xml ./service-registry/
RUN mvn -B dependency:resolve

COPY service-registry/src ./service-registry/src
RUN cd service-registry && mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/service-registry/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
