#!/bin/bash

echo ""
echo "========================================"
echo "PostgreSQL Database Setup"
echo "========================================"
echo ""

# Check if PostgreSQL is installed
if ! command -v psql &> /dev/null; then
    echo "ERROR: PostgreSQL is not installed"
    echo "Please install PostgreSQL first: https://www.postgresql.org/download/"
    exit 1
fi

echo "PostgreSQL found. Setting up databases..."
echo ""

# Create the insurance user and databases
psql -U postgres -f init-db.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Setup completed successfully!"
    echo "========================================"
    echo ""
    echo "Created databases:"
    echo " - policy_service"
    echo " - claims_service"
    echo " - customer_service"
    echo ""
    echo "User: insurance"
    echo "Password: insurance123"
    echo ""
    echo "Next steps:"
    echo "1. Build the project: mvn clean install"
    echo "2. Start Service Registry first"
    echo "3. Then start each microservice"
    echo ""
else
    echo ""
    echo "ERROR: Database setup failed"
    echo "Please check that PostgreSQL is running"
    echo ""
fi
