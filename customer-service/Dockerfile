FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY customer-service/pom.xml ./customer-service/
RUN mvn -B dependency:resolve

COPY customer-service/src ./customer-service/src
RUN cd customer-service && mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/customer-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
