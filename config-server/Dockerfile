FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY config-server/pom.xml ./config-server/
RUN mvn -B dependency:resolve

COPY config-server/src ./config-server/src
RUN cd config-server && mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
