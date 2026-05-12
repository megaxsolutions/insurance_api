# Quick Reference: PostgreSQL + Hibernate

## Database Credentials
- **Host:** localhost or postgres (Docker)
- **Port:** 5432
- **User:** insurance
- **Password:** insurance123

## Quick Commands

### Start Everything with Docker
```bash
docker-compose up --build
```

### Stop Everything
```bash
docker-compose down
```

### View PostgreSQL Logs
```bash
docker-compose logs -f postgres
```

### Access PostgreSQL CLI
```bash
psql -U insurance -d policy_service
```

### List Tables
```bash
\dt  # Inside psql
```

### Check Table Schema
```bash
\d+ policies
\d+ claims
\d+ customers
```

### Query Data
```bash
SELECT * FROM customers;
SELECT COUNT(*) FROM policies;
SELECT * FROM claims WHERE status = 'PENDING';
```

### Exit PostgreSQL
```bash
\q
```

## Service Ports
- API Gateway: 8080
- Policy Service: 8081
- Claims Service: 8082
- Customer Service: 8083
- Notification Service: 8084
- Eureka: 8761
- PostgreSQL: 5432

## Build & Run Locally

### Build All Services
```bash
mvn clean install
```

### Run Individual Services
```bash
# Terminal 1: Service Registry
cd service-registry && mvn spring-boot:run

# Terminal 2: API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 3: Policy Service
cd policy-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Terminal 4: Claims Service
cd claims-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Terminal 5: Customer Service
cd customer-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Terminal 6: Notification Service
cd notification-service && mvn spring-boot:run
```

## Test API Endpoints

### Create Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"John",
    "lastName":"Doe",
    "email":"john@example.com",
    "phoneNumber":"555-1234",
    "address":"123 Main St",
    "city":"Springfield",
    "state":"IL",
    "zipCode":"62701"
  }'
```

### Get All Customers
```bash
curl http://localhost:8080/api/customers
```

### Create Policy
```bash
curl -X POST http://localhost:8080/api/policies \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber":"POL-001",
    "customerId":1,
    "policyType":"AUTO",
    "premiumAmount":1200.00,
    "status":"ACTIVE",
    "startDate":"2024-01-01",
    "endDate":"2025-01-01"
  }'
```

### Create Claim
```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimNumber":"CLM-001",
    "policyId":1,
    "claimType":"ACCIDENT",
    "claimAmount":5000.00,
    "status":"PENDING",
    "description":"Car accident claim",
    "submissionDate":"2024-01-15"
  }'
```

### Get Claims by Status
```bash
curl http://localhost:8080/api/claims/status/PENDING
```

## Database Files Location
- Schema Creation: `init-db.sql`
- Setup Script (Windows): `setup-postgres.bat`
- Setup Script (Unix): `setup-postgres.sh`

## Configuration Files
- Global: `pom.xml`
- Policy Service: `policy-service/src/main/resources/application.yml`
- Claims Service: `claims-service/src/main/resources/application.yml`
- Customer Service: `customer-service/src/main/resources/application.yml`

## Hibernate DDL Options
- `create-drop` - Create tables, drop on shutdown (development)
- `create` - Create tables on startup
- `update` - Alter existing tables
- `validate` - Only validate schema (production)
- `none` - No DDL operations

## Docker Commands

### Build Specific Service
```bash
docker-compose build policy-service
```

### Rebuild All
```bash
docker-compose up --build
```

### Run in Detached Mode
```bash
docker-compose up -d
```

### View Specific Service Logs
```bash
docker-compose logs -f policy-service
```

### Execute Command in Container
```bash
docker-compose exec postgres psql -U insurance
```

### Remove All Volumes (Clean Reset)
```bash
docker-compose down -v
```

## Troubleshooting

### Can't Connect to PostgreSQL
```bash
# Check if PostgreSQL is running
docker-compose ps
# Should see postgres service with status "Up"

# Check PostgreSQL logs
docker-compose logs postgres
```

### Tables Not Created
```bash
# Verify service is running and connected
docker-compose logs policy-service | grep "Creating"

# Manually check tables
docker-compose exec postgres psql -U insurance -d policy_service -c "\dt"
```

### Port Already in Use
```bash
# Change port in docker-compose.yml or service config
# Example for PostgreSQL port 5432:
# Map to different port: "5433:5432"
```

### Reset Everything
```bash
# Full reset
docker-compose down -v
docker-compose up --build

# Or just postgres
docker-compose exec postgres dropdb -U insurance policy_service
docker-compose exec postgres createdb -U insurance policy_service
```

## Monitoring

### Check PostgreSQL Connection Pool
```bash
# Inside any service logs, look for:
# "HikariPool-1 - Starting pool"
# "HikariPool-1 - Added connection"
```

### Monitor Database Size
```bash
docker-compose exec postgres psql -U insurance -c \
  "SELECT datname, pg_size_pretty(pg_database_size(datname)) 
   FROM pg_database 
   WHERE datname IN ('policy_service', 'claims_service', 'customer_service');"
```

### View Active Connections
```bash
docker-compose exec postgres psql -U insurance -c \
  "SELECT * FROM pg_stat_activity WHERE datname IN ('policy_service', 'claims_service', 'customer_service');"
```

## Documentation

- **POSTGRES_SETUP.md** - Complete PostgreSQL setup guide
- **HIBERNATE_POSTGRESQL_CONFIG.md** - Detailed Hibernate configuration
- **IMPLEMENTATION_SUMMARY.md** - Summary of all changes
- **README.md** - General project documentation

## Useful Links

- PostgreSQL: https://www.postgresql.org
- Hibernate: https://hibernate.org
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Boot: https://spring.io/projects/spring-boot
