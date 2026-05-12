#!/bin/bash

echo "Building Insurance Microservices Project..."

# Build parent and all modules
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Build completed successfully!"
    echo "========================================"
    echo ""
    echo "Services are ready to run:"
    echo ""
    echo "Start in separate terminals:"
    echo "1. cd service-registry && mvn spring-boot:run"
    echo "2. cd config-server && mvn spring-boot:run"
    echo "3. cd api-gateway && mvn spring-boot:run"
    echo "4. cd policy-service && mvn spring-boot:run"
    echo "5. cd claims-service && mvn spring-boot:run"
    echo "6. cd customer-service && mvn spring-boot:run"
    echo "7. cd notification-service && mvn spring-boot:run"
    echo ""
    echo "Or use Docker Compose:"
    echo "docker-compose up --build"
else
    echo "Build failed. Check logs above for errors."
fi
