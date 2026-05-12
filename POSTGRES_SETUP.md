# PostgreSQL Setup Guide

## Local Development Setup

### Prerequisites
- PostgreSQL 13+ installed and running
- psql command-line tool

### Installation

#### Windows
1. Download PostgreSQL from https://www.postgresql.org/download/windows/
2. Run the installer and note the password for the `postgres` user
3. Ensure PostgreSQL service is running

#### macOS
```bash
brew install postgresql
brew services start postgresql
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### Setup Databases and User

#### Option 1: Using SQL Script
```bash
psql -U postgres -f init-db.sql
```

#### Option 2: Manual Setup
```bash
# Connect to PostgreSQL as postgres user
psql -U postgres

# Create insurance user
CREATE USER insurance WITH PASSWORD 'insurance123';

# Create databases
CREATE DATABASE policy_service OWNER insurance;
CREATE DATABASE claims_service OWNER insurance;
CREATE DATABASE customer_service OWNER insurance;

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE policy_service TO insurance;
GRANT ALL PRIVILEGES ON DATABASE claims_service TO insurance;
GRANT ALL PRIVILEGES ON DATABASE customer_service TO insurance;

# Exit psql
\q
```

### Verify Setup

```bash
# Connect as insurance user
psql -U insurance -d policy_service -h localhost

# List all databases
\l

# List all tables
\dt

# Quit
\q
```

### Connection Parameters

- **Host:** localhost
- **Port:** 5432
- **Username:** insurance
- **Password:** insurance123
- **Databases:**
  - policy_service
  - claims_service
  - customer_service

### Running Services with PostgreSQL

#### Local Development (Single Machine)
```bash
# Start PostgreSQL first
# Windows
pg_ctl -D "C:\Program Files\PostgreSQL\15\data" start

# Or use Services app

# Then start each service
cd policy-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
cd claims-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
cd customer-service && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

#### Docker Compose (Recommended)
```bash
docker-compose up -d postgres
docker-compose up -d --build

# Check logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Useful PostgreSQL Commands

```bash
# Connect to a specific database
psql -U insurance -d policy_service

# Backup a database
pg_dump -U insurance -d policy_service > backup.sql

# Restore from backup
psql -U insurance -d policy_service < backup.sql

# Drop a database
psql -U postgres -c "DROP DATABASE policy_service;"

# View database size
SELECT datname, pg_size_pretty(pg_database_size(datname)) 
FROM pg_database 
WHERE datname IN ('policy_service', 'claims_service', 'customer_service');
```

### Troubleshooting

**Error: FATAL:  Ident authentication failed for user "insurance"**
- Edit PostgreSQL configuration to use md5 or scram-sha-256 authentication
- Windows: Edit `C:\Program Files\PostgreSQL\15\data\pg_hba.conf`
- macOS/Linux: Edit `/usr/local/var/postgres/pg_hba.conf` or `/var/lib/postgresql/15/main/pg_hba.conf`
- Change `ident` to `md5` or `scram-sha-256`

**Error: Connection refused**
- Ensure PostgreSQL service is running
- Check if port 5432 is not blocked by firewall
- Verify PostgreSQL is listening on localhost:5432

**Error: database "policy_service" does not exist**
- Run the init-db.sql script
- Or create databases manually using psql

### Hibernate DDL Settings

Current configuration: `hibernate.ddl-auto: create-drop`
- `create-drop`: Creates tables on startup, drops on shutdown (development)
- `create`: Creates tables on startup (keeps existing data)
- `update`: Updates existing tables with new columns
- `validate`: Only validates schema matches entities
- `none`: No DDL operations

For production, use `validate` or `update`.

### Next Steps

1. Build the project: `mvn clean install`
2. Start services with PostgreSQL
3. Test endpoints through API Gateway (http://localhost:8080)
