# Insurance Microservices Architecture

A comprehensive Spring Boot microservices application for Insurance API endpoints with a complete microservices architecture.

## Project Overview

This project demonstrates a production-ready microservices architecture for an insurance system with multiple independent services communicating through REST APIs and service discovery.

## Services

### 1. **Service Registry (Eureka Server)** - Port 8761
   - Centralized service registry for service discovery
   - Enables automatic service registration and discovery
   - Facilitates load balancing across instances

### 2. **Config Server** - Port 8888
   - Centralized configuration management
   - Supports Git-based configuration
   - Dynamic property updates without restart

### 3. **API Gateway** - Port 8080
   - Single entry point for all client requests
   - Request routing to appropriate microservices
   - Load balancing and circuit breaking capabilities
   - Built with Spring Cloud Gateway

### 4. **Policy Service** - Port 8081
   - Manages insurance policies
   - **Endpoints:**
     - `GET /api/policies` - Get all policies
     - `GET /api/policies/{id}` - Get policy by ID
     - `GET /api/policies/customer/{customerId}` - Get policies by customer
     - `POST /api/policies` - Create new policy
     - `PUT /api/policies/{id}` - Update policy
     - `DELETE /api/policies/{id}` - Delete policy

### 5. **Claims Service** - Port 8082
   - Handles insurance claims
   - **Endpoints:**
     - `GET /api/claims` - Get all claims
     - `GET /api/claims/{id}` - Get claim by ID
     - `GET /api/claims/policy/{policyId}` - Get claims by policy
     - `GET /api/claims/status/{status}` - Get claims by status
     - `POST /api/claims` - Create new claim
     - `PUT /api/claims/{id}` - Update claim
     - `DELETE /api/claims/{id}` - Delete claim

### 6. **Customer Service** - Port 8083
   - Manages customer information
   - **Endpoints:**
     - `GET /api/customers` - Get all customers
     - `GET /api/customers/{id}` - Get customer by ID
     - `GET /api/customers/email/{email}` - Get customer by email
     - `GET /api/customers/city/{city}` - Get customers by city
     - `POST /api/customers` - Create new customer
     - `PUT /api/customers/{id}` - Update customer
     - `DELETE /api/customers/{id}` - Delete customer

### 7. **Notification Service** - Port 8084
   - Handles notifications
   - **Endpoints:**
     - `POST /api/notifications/send` - Send notification
     - `GET /api/notifications/status/{notificationId}` - Get notification status

## Architecture

```
┌─────────────────┐
│   Client        │
└────────┬────────┘
         │
    ┌────▼─────────────────────┐
    │   API Gateway (8080)     │
    │   Spring Cloud Gateway   │
    └────┬──────┬──────┬───────┘
         │      │      │
    ┌────▼─┐ ┌──▼───┐ ┌▼──────┐
    │Policy│ │Claims│ │Customer│
    │ Svc  │ │ Svc  │ │  Svc   │
    │(8081)│ │(8082)│ │ (8083) │
    └──────┘ └──────┘ └────────┘
         │      │      │
    ┌────▼──────▼──────▼────┐
    │  Service Registry     │
    │  Eureka (8761)        │
    └──────────────────────┘
         │
    ┌────▼──────────────────┐
    │  Config Server (8888) │
    └──────────────────────┘
```

## Prerequisites

- Java 17 or higher
- Maven 3.8.1 or higher
- PostgreSQL 13+ (for local development)
- Docker & Docker Compose (optional, for containerized deployment)

## Getting Started

### Database Setup

#### Local Development with PostgreSQL

1. **Install PostgreSQL:** https://www.postgresql.org/download/

2. **Run setup script:**
   - Windows: `setup-postgres.bat`
   - macOS/Linux: `bash setup-postgres.sh`

   Or manually run: `psql -U postgres -f init-db.sql`

3. **Verify database connection:**
   ```bash
   psql -U insurance -d policy_service -h localhost
   ```

See [POSTGRES_SETUP.md](POSTGRES_SETUP.md) for detailed setup instructions.

### Local Development

1. **Navigate to project directory:**
   ```bash
   cd insurance-microservices
   ```

2. **Build the entire project:**
   ```bash
   mvn clean install
   ```

3. **Start services (in separate terminals):**

   Start Service Registry (first):
   ```bash
   cd service-registry
   mvn spring-boot:run
   ```

   Start API Gateway:
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

   Start Policy Service (with local PostgreSQL):
   ```bash
   cd policy-service
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
   ```

   Start Claims Service:
   ```bash
   cd claims-service
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
   ```

   Start Customer Service:
   ```bash
   cd customer-service
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
   ```

   Start Notification Service:
   ```bash
   cd notification-service
   mvn spring-boot:run
   ```

### Docker Deployment

1. **Build and run all services using Docker Compose:**
   ```bash
   docker-compose up --build
   ```

   This will automatically:
   - Start PostgreSQL container
   - Create databases (policy_service, claims_service, customer_service)
   - Create insurance user with credentials
   - Start all microservices with proper database connections

2. **Stop services:**
   ```bash
   docker-compose down
   ```

3. **View logs:**
   ```bash
   docker-compose logs -f service-name
   ```

### Database Configuration

**PostgreSQL Connection Details:**
- Host: localhost:5432 (local) or postgres:5432 (Docker)
- Username: insurance
- Password: insurance123
- Databases:
  - policy_service (Policy Service)
  - claims_service (Claims Service)
  - customer_service (Customer Service)

**Hibernate Configuration:**
- Dialect: PostgreSQLDialect
- DDL Strategy: create-drop (development), use `validate` for production
- Connection Pooling: HikariCP (default)

### Database Schema

Tables are automatically created by Hibernate when services start:

**Policies Table:**
- id (PK), policy_number (unique), customer_id, policy_type, premium_amount, status, start_date, end_date, created_at, updated_at

**Claims Table:**
- id (PK), claim_number (unique), policy_id, claim_type, claim_amount, status, description, submission_date, approval_date, created_at, updated_at

**Customers Table:**
- id (PK), first_name, last_name, email (unique), phone_number, date_of_birth, address, city, state, zip_code, created_at, updated_at

## Testing the APIs

### Using cURL

1. **Create a Customer:**
   ```bash
   curl -X POST http://localhost:8080/api/customers \
     -H "Content-Type: application/json" \
     -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","phoneNumber":"555-1234"}'
   ```

2. **Create a Policy:**
   ```bash
   curl -X POST http://localhost:8080/api/policies \
     -H "Content-Type: application/json" \
     -d '{"policyNumber":"POL-001","customerId":1,"policyType":"AUTO","premiumAmount":1200.00,"status":"ACTIVE"}'
   ```

3. **Get All Customers:**
   ```bash
   curl http://localhost:8080/api/customers
   ```

4. **Create a Claim:**
   ```bash
   curl -X POST http://localhost:8080/api/claims \
     -H "Content-Type: application/json" \
     -d '{"claimNumber":"CLM-001","policyId":1,"claimType":"ACCIDENT","claimAmount":5000.00,"status":"PENDING"}'
   ```

### Using Postman

1. Import the collection from the `postman/` directory
2. Configure environment variables (base URL, service ports)
3. Execute requests

### Service Discovery

Access the Eureka Server Dashboard:
- URL: `http://localhost:8761`

## Configuration

### Service Registry (Eureka)
- Edit `service-registry/src/main/resources/application.yml`

### API Gateway Routes
- Edit `api-gateway/src/main/resources/application.yml`
- Add new routes for additional services

### Individual Service Configuration
- Each service has its own `application.yml` file
- `application-local.yml` for local PostgreSQL development
- Modify ports, database URLs, and other settings as needed

## Database & Hibernate

**Database:** PostgreSQL 13+
**ORM:** Hibernate via Spring Data JPA
**Dialect:** PostgreSQLDialect

### Hibernate Configuration Properties

```yaml
spring.jpa.hibernate.ddl-auto: create-drop  # Development
spring.jpa.show-sql: false                  # Don't log SQL
spring.jpa.open-in-view: false              # No lazy loading in views
hibernate.format_sql: true                  # Pretty print SQL
hibernate.jdbc.batch_size: 20               # Batch inserts
```

### DDL Auto Strategies
- `create-drop`: Creates at startup, drops at shutdown (development)
- `create`: Creates at startup, keeps data
- `update`: Alters tables to match entities
- `validate`: Only validates schema
- `none`: No DDL operations (production)

For production, use `validate` and manage schema separately.

## Dependencies

### Spring Boot 3.1.5
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Cloud Starter Netflix Eureka
- Spring Cloud Starter Gateway
- PostgreSQL Driver

### Spring Cloud 2022.0.4
- Spring Cloud Config Server
- Spring Cloud Netflix Eureka

## Project Structure

```
insurance-microservices/
├── pom.xml (Parent POM)
├── docker-compose.yml
├── init-db.sql
├── POSTGRES_SETUP.md
├── setup-postgres.bat / setup-postgres.sh
├── service-registry/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── config-server/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── api-gateway/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── policy-service/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── application.yml
│   ├── application-local.yml
│   └── src/
├── claims-service/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── application.yml
│   ├── application-local.yml
│   └── src/
├── customer-service/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── application.yml
│   ├── application-local.yml
│   └── src/
└── notification-service/
    ├── pom.xml
    ├── Dockerfile
    └── src/
```

## Troubleshooting

### PostgreSQL Connection Issues
- Error: `connection refused`
  - Ensure PostgreSQL is running
  - Verify credentials (insurance/insurance123)
  - Check host/port: localhost:5432

- Error: `database "policy_service" does not exist`
  - Run: `psql -U postgres -f init-db.sql`
  - Or run setup script: `setup-postgres.bat` / `setup-postgres.sh`

- Error: `Ident authentication failed`
  - Edit pg_hba.conf and change `ident` to `md5` or `scram-sha-256`
  - Restart PostgreSQL

### Hibernate Issues
- Error: `Tables not created`
  - Ensure `spring.jpa.hibernate.ddl-auto: create-drop`
  - Check Hibernate dialect is PostgreSQLDialect
  - Verify service can connect to database

### Port Already in Use
- Modify port in service's `application.yml` file
- Or kill process using the port

### Eureka Registration Issues
- Ensure Service Registry is running first
- Check Eureka URL is correct: http://localhost:8761/eureka/
- Verify service names are unique

### Docker Issues
- Error: `postgres service unhealthy`
  - Check postgres logs: `docker-compose logs postgres`
  - Wait longer for postgres to initialize
  - Verify volume is mounted

- Error: `Cannot connect to docker daemon`
  - Ensure Docker Desktop is running (Windows/macOS)
  - Check Docker permissions (Linux)

## Next Steps

1. **Add Security:** Implement Spring Security and OAuth2/JWT
2. **Add Inter-service Communication:** Implement Feign clients for service-to-service calls
3. **Add Circuit Breaker:** Implement Resilience4j for fault tolerance
4. **Add Logging:** Implement centralized logging with ELK Stack
5. **Add Monitoring:** Integrate Prometheus and Grafana
6. **Add Message Queue:** Implement RabbitMQ or Kafka for async communication
7. **Add Database:** Replace H2 with PostgreSQL or MySQL

## License

This project is licensed under the MIT License.

## Support

For issues or questions, please create an issue in the repository.
