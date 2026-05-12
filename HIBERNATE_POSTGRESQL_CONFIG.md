# PostgreSQL & Hibernate Configuration Summary

## Overview
The Insurance Microservices application has been configured to use PostgreSQL with Hibernate ORM for persistent data storage.

## What Has Been Configured

### 1. Dependencies
Each service (Policy, Claims, Customer) now includes:
- PostgreSQL JDBC Driver
- Spring Data JPA (with Hibernate)
- Lombok for entity annotations

### 2. Database Connection
**Local Development (application.yml and application-local.yml):**
```
URL: jdbc:postgresql://localhost:5432/{service_name}
Username: insurance
Password: insurance123
Driver: org.postgresql.Driver
Dialect: org.hibernate.dialect.PostgreSQLDialect
```

**Docker Deployment (docker-compose.yml):**
```
URL: jdbc:postgresql://postgres:5432/{service_name}
Same credentials as local
Automatic database creation via init-db.sql
```

### 3. Hibernate Configuration
**DDL Auto Strategy:** `create-drop` (for development)
- Creates tables on startup
- Drops tables on shutdown
- Perfect for development and testing

**Key Properties:**
- `format_sql: true` - Pretty print SQL queries
- `jdbc.batch_size: 20` - Batch inserts for performance
- `jdbc.fetch_size: 50` - Optimize query result fetching

### 4. Database Schema
Three separate PostgreSQL databases:
1. **policy_service** - Policies table with policy information
2. **claims_service** - Claims table with claim information
3. **customer_service** - Customers table with customer information

All tables include:
- Primary key (auto-generated ID)
- Unique constraints (policyNumber, claimNumber, email)
- Column-level constraints (nullable, length, etc.)
- Audit timestamps (created_at, updated_at)

### 5. Entities with Enhanced Hibernate Annotations

#### Policy Entity
- Table: policies
- Columns: id, policy_number, customer_id, policy_type, premium_amount, status, start_date, end_date, created_at, updated_at
- Unique: policy_number
- Constraints: policy_number and status not null

#### Claim Entity
- Table: claims
- Columns: id, claim_number, policy_id, claim_type, claim_amount, status, description, submission_date, approval_date, created_at, updated_at
- Unique: claim_number
- Constraints: claim_number and status not null

#### Customer Entity
- Table: customers
- Columns: id, first_name, last_name, email, phone_number, date_of_birth, address, city, state, zip_code, created_at, updated_at
- Unique: email
- Constraints: first_name, last_name, email not null

### 6. Docker Compose Integration
**postgres Service:**
- Image: postgres:15-alpine (lightweight)
- Volume: postgres_data (persistent storage)
- Health Check: Automatic readiness probe
- Initialization: Runs init-db.sql on startup

**Service Dependencies:**
- Policy, Claims, Customer services depend on PostgreSQL
- Wait for postgres service to be healthy before starting

## Quick Start

### Option 1: Local PostgreSQL
```bash
# 1. Install PostgreSQL from https://www.postgresql.org/download/
# 2. Run setup script
setup-postgres.bat  # Windows
# or
bash setup-postgres.sh  # Linux/macOS

# 3. Build project
mvn clean install

# 4. Start services
cd service-registry && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd policy-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### Option 2: Docker Compose (Recommended)
```bash
# PostgreSQL and all services start automatically
docker-compose up --build
```

## Files Created/Modified

### New Files:
- `init-db.sql` - Initializes PostgreSQL databases
- `setup-postgres.bat` - Windows setup script
- `setup-postgres.sh` - Linux/macOS setup script
- `POSTGRES_SETUP.md` - Detailed setup guide
- `application-local.yml` - Local development configs (3 services)

### Modified Files:
- `pom.xml` - Added Lombok dependency (parent)
- `policy-service/pom.xml` - PostgreSQL driver
- `claims-service/pom.xml` - PostgreSQL driver
- `customer-service/pom.xml` - PostgreSQL driver
- `policy-service/src/main/java/com/insurance/policy/entity/Policy.java` - Enhanced Hibernate annotations
- `claims-service/src/main/java/com/insurance/claims/entity/Claim.java` - Enhanced Hibernate annotations
- `customer-service/src/main/java/com/insurance/customer/entity/Customer.java` - Enhanced Hibernate annotations
- `application.yml` files (3 services) - PostgreSQL configuration
- `docker-compose.yml` - PostgreSQL service + database initialization
- `README.md` - Database setup documentation

## Verification Steps

1. **PostgreSQL Running:**
   ```bash
   psql -U insurance -d policy_service -h localhost
   ```

2. **Databases Created:**
   ```bash
   psql -U insurance -l
   ```
   Should show: policy_service, claims_service, customer_service

3. **Tables Auto-Created:**
   After starting services, tables should exist:
   ```bash
   psql -U insurance -d policy_service
   \dt  # List tables
   ```

4. **Test Data:**
   ```bash
   curl -X POST http://localhost:8080/api/customers \
     -H "Content-Type: application/json" \
     -d '{"firstName":"John","lastName":"Doe","email":"john@example.com"}'
   ```

## Deployment Considerations

**For Production:**
1. Change `ddl-auto` from `create-drop` to `validate`
2. Use managed database (AWS RDS, Azure Database, etc.)
3. Implement connection pooling optimization
4. Set up database backups
5. Enable SSL connections
6. Use secrets management for credentials

**Example Production Config:**
```yaml
spring.jpa.hibernate.ddl-auto: validate
spring.datasource.url: jdbc:postgresql://prod-db.aws.com:5432/policy_service
spring.datasource.hikari.maximum-pool-size: 20
spring.datasource.hikari.minimum-idle: 5
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Check PostgreSQL is running on localhost:5432 |
| Authentication failed | Verify user "insurance" exists with correct password |
| Database doesn't exist | Run init-db.sql or setup scripts |
| Tables not created | Ensure Hibernate ddl-auto is set correctly |
| Docker postgres unhealthy | Check logs: `docker-compose logs postgres` |

## Next Steps

1. Test the complete application with Docker Compose
2. Implement database migrations with Flyway
3. Add database performance monitoring
4. Set up backup strategy
5. Implement connection pooling tuning
