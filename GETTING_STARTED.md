# Getting Started with PostgreSQL + Hibernate

## TL;DR - Quick Start

### Using Docker Compose (Recommended)
```bash
cd insurance-microservices
docker-compose up --build
```

That's it! PostgreSQL and all services start automatically.

### Using Local PostgreSQL
```bash
# 1. Setup databases
bash setup-postgres.sh  # Windows: setup-postgres.bat

# 2. Build
mvn clean install

# 3. Start services
cd service-registry && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run  # Terminal 2
cd policy-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"  # Terminal 3
```

---

## What Was Changed

### ✅ Database Layer
- **Replaced:** H2 in-memory database
- **With:** PostgreSQL 13+
- **Benefit:** Persistent data storage, production-ready

### ✅ ORM Layer
- **Used:** Hibernate via Spring Data JPA
- **Features:** Auto schema creation, entity lifecycle management, query optimization

### ✅ Entities Enhanced
- **Policy:** Added 10+ JPA annotations, timestamps, constraints
- **Claim:** Added proper column definitions, unique constraints
- **Customer:** Added validation constraints, audit fields

### ✅ Docker Support
- **PostgreSQL Container:** Automatic database initialization
- **Services:** Environment-aware database URLs
- **Networking:** All services connected in isolated network

---

## Architecture Overview

```
┌─────────────────────────────────────────┐
│   Client Applications                   │
└──────────────┬──────────────────────────┘
               │
    ┌──────────▼──────────┐
    │  API Gateway (8080) │
    └──────┬──────┬───────┘
           │      │
     ┌─────▼─┐┌──▼──────┐┌─────────┐
     │Policy ││ Claims  ││Customer │
     │Svc 81 ││ Svc 82  ││ Svc 83  │
     └─────┬─┘└──┬──────┘└────┬────┘
           │     │            │
     ┌─────▼─────▼────────────▼────┐
     │  PostgreSQL Server (5432)   │
     ├─────────────────────────────┤
     │ ├─ policy_service DB        │
     │ ├─ claims_service DB        │
     │ └─ customer_service DB      │
     └─────────────────────────────┘
```

---

## Database Structure

### Three Independent Databases
Each service has its own PostgreSQL database to ensure independence:

```
PostgreSQL Server
├── policy_service
│   └── policies table
│       ├─ id, policy_number, customer_id, policy_type
│       ├─ premium_amount, status, start_date, end_date
│       └─ created_at, updated_at (audit fields)
│
├── claims_service
│   └── claims table
│       ├─ id, claim_number, policy_id, claim_type
│       ├─ claim_amount, status, description
│       ├─ submission_date, approval_date
│       └─ created_at, updated_at (audit fields)
│
└── customer_service
    └── customers table
        ├─ id, first_name, last_name, email
        ├─ phone_number, date_of_birth, address
        ├─ city, state, zip_code
        └─ created_at, updated_at (audit fields)
```

---

## Connection Details

**For Local Development:**
- Host: localhost
- Port: 5432
- Username: insurance
- Password: insurance123

**For Docker:**
- Host: postgres (Docker hostname)
- Port: 5432
- Username: insurance
- Password: insurance123

**Databases:**
- policy_service (Port 8081)
- claims_service (Port 8082)
- customer_service (Port 8083)

---

## Service Ports

| Service | Port | Database |
|---------|------|----------|
| Eureka Registry | 8761 | None |
| API Gateway | 8080 | None |
| Policy Service | 8081 | policy_service |
| Claims Service | 8082 | claims_service |
| Customer Service | 8083 | customer_service |
| Notification Service | 8084 | None |
| PostgreSQL | 5432 | All |

---

## Directory Structure

```
insurance-microservices/
├── init-db.sql                          # Database initialization
├── setup-postgres.sh                    # Unix setup script
├── setup-postgres.bat                   # Windows setup script
├── docker-compose.yml                   # Docker orchestration
├── pom.xml                              # Parent Maven config
│
├── POSTGRES_SETUP.md                    # PostgreSQL installation guide
├── HIBERNATE_POSTGRESQL_CONFIG.md       # Hibernate configuration details
├── IMPLEMENTATION_SUMMARY.md            # What was changed
├── QUICK_REFERENCE.md                   # Commands reference
├── VERIFICATION_CHECKLIST.md            # Implementation checklist
├── README.md                            # Main documentation
│
├── policy-service/                      # Policy microservice
│   ├── src/main/resources/
│   │   ├── application.yml              # Docker/Production config
│   │   └── application-local.yml        # Local development config
│   └── src/main/java/com/insurance/policy/
│       ├── entity/Policy.java           # Enhanced with Hibernate annotations
│       ├── repository/PolicyRepository.java
│       └── controller/PolicyController.java
│
├── claims-service/                      # Claims microservice
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-local.yml
│   └── src/main/java/com/insurance/claims/
│       ├── entity/Claim.java            # Enhanced with Hibernate annotations
│       ├── repository/ClaimRepository.java
│       └── controller/ClaimController.java
│
├── customer-service/                    # Customer microservice
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-local.yml
│   └── src/main/java/com/insurance/customer/
│       ├── entity/Customer.java         # Enhanced with Hibernate annotations
│       ├── repository/CustomerRepository.java
│       └── controller/CustomerController.java
│
└── [other services...]
```

---

## Hibernate Features

### Auto-Generated Schema
Tables are automatically created when services start:
```sql
-- Policy Service
CREATE TABLE policies (
    id BIGSERIAL PRIMARY KEY,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    policy_type VARCHAR(50) NOT NULL,
    premium_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date VARCHAR,
    end_date VARCHAR,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Similar for claims and customers tables
```

### Lifecycle Callbacks
Every entity has audit timestamp management:
```java
@PrePersist  // Called before INSERT
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate   // Called before UPDATE
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

### Batch Processing
Configured for optimal performance:
```
Batch insert/update: 20 statements
Query fetch size: 50 rows
Connection pooling: HikariCP (20 connections)
```

---

## Testing the System

### 1. Verify Services are Running
```bash
# Check service availability
curl http://localhost:8761  # Eureka should respond
curl http://localhost:8080  # Gateway should be available
```

### 2. Create Test Data

**Create a Customer:**
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"John",
    "lastName":"Doe",
    "email":"john@example.com",
    "phoneNumber":"555-1234",
    "city":"Springfield"
  }'
```

**Create a Policy:**
```bash
curl -X POST http://localhost:8080/api/policies \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber":"POL-001",
    "customerId":1,
    "policyType":"AUTO",
    "premiumAmount":1200.00,
    "status":"ACTIVE"
  }'
```

**Create a Claim:**
```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimNumber":"CLM-001",
    "policyId":1,
    "claimType":"ACCIDENT",
    "claimAmount":5000.00,
    "status":"PENDING"
  }'
```

### 3. Verify Database

**Connect to PostgreSQL:**
```bash
# Docker
docker-compose exec postgres psql -U insurance

# Local
psql -U insurance -d policy_service
```

**Check tables were created:**
```sql
\dt  -- List all tables

-- View data
SELECT * FROM customers;
SELECT * FROM policies;
SELECT * FROM claims;

-- Check schema
\d+ customers;  -- Show column details
```

---

## Troubleshooting

### PostgreSQL Won't Start
```bash
# Check if port 5432 is available
sudo lsof -i :5432  # Linux/macOS
netstat -ano | findstr :5432  # Windows

# Change port in docker-compose.yml if needed
# "5433:5432" instead of "5432:5432"
```

### Services Can't Connect to PostgreSQL
```bash
# Check PostgreSQL is healthy
docker-compose ps

# Check PostgreSQL logs
docker-compose logs postgres

# Verify connection
docker-compose exec postgres psql -U insurance -c "\l"
```

### Tables Not Created
```bash
# Check service logs for Hibernate output
docker-compose logs policy-service | grep -i hibernate

# Manually trigger table creation
docker-compose restart policy-service

# Verify tables exist
docker-compose exec postgres psql -U insurance -d policy_service -c "\dt"
```

### Port Already in Use
```bash
# Change port in docker-compose.yml or application.yml
# Example: Change 8081 to 8091 for policy service
```

---

## Configuration Files

### Development (application-local.yml)
For local PostgreSQL development with detailed output:
```yaml
spring.jpa.show-sql: false          # Don't log every SQL
spring.jpa.hibernate.ddl-auto: create-drop  # Recreate on restart
hibernate.format_sql: true          # Pretty print SQL
hibernate.jdbc.batch_size: 20       # Batch operations
```

### Production (application.yml)
For Docker/Production with performance tuning:
```yaml
spring.jpa.show-sql: false          # Don't log SQL
spring.jpa.hibernate.ddl-auto: create-drop  # Docker resets on restart
spring.jpa.open-in-view: false      # No lazy loading in views
```

For actual production, change to:
```yaml
spring.jpa.hibernate.ddl-auto: validate  # Don't modify schema
```

---

## Next Steps

### 1. Immediate (Today)
- [x] Review this guide
- [x] Run: `docker-compose up --build`
- [x] Test API endpoints with curl
- [x] Verify data persists in PostgreSQL

### 2. Short Term (This Week)
- [ ] Implement additional services
- [ ] Add inter-service communication (Feign clients)
- [ ] Add circuit breaker (Resilience4j)
- [ ] Implement logging (SLF4J, ELK)

### 3. Medium Term (This Month)
- [ ] Add authentication (Spring Security)
- [ ] Implement audit logging
- [ ] Add database migrations (Flyway)
- [ ] Set up CI/CD pipeline

### 4. Long Term (Production)
- [ ] Use managed database (AWS RDS, Azure DB)
- [ ] Implement replication and backups
- [ ] Set up monitoring and alerts
- [ ] Performance tuning and optimization

---

## Key Files Reference

- **QUICK_REFERENCE.md** - Common commands and examples
- **POSTGRES_SETUP.md** - Database installation steps
- **HIBERNATE_POSTGRESQL_CONFIG.md** - Configuration options
- **IMPLEMENTATION_SUMMARY.md** - What changed and why
- **README.md** - General project documentation

---

## Support

For detailed information, refer to:
1. **POSTGRES_SETUP.md** - PostgreSQL-specific issues
2. **QUICK_REFERENCE.md** - Common commands
3. **IMPLEMENTATION_SUMMARY.md** - Configuration details
4. **README.md** - General troubleshooting

---

**Happy Coding!** 🚀

Your Insurance Microservices application is now fully configured with PostgreSQL and Hibernate.
