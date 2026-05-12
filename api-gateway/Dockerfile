FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
RUN mvn -B dependency:resolve

COPY api-gateway/src ./api-gateway/src
RUN cd api-gateway && mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
