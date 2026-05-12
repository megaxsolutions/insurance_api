-- Create databases for each service
CREATE DATABASE policy_service;
CREATE DATABASE claims_service;
CREATE DATABASE customer_service;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE policy_service TO insurance;
GRANT ALL PRIVILEGES ON DATABASE claims_service TO insurance;
GRANT ALL PRIVILEGES ON DATABASE customer_service TO insurance;
