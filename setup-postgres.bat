@echo off
REM PostgreSQL Setup Script for Windows

echo.
echo ========================================
echo PostgreSQL Database Setup
echo ========================================
echo.

REM Check if PostgreSQL is installed
where psql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: PostgreSQL is not installed or psql is not in PATH
    echo Please install PostgreSQL first: https://www.postgresql.org/download/windows/
    pause
    exit /b 1
)

echo PostgreSQL found. Setting up databases...
echo.

REM Create the insurance user and databases
psql -U postgres -f init-db.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Setup completed successfully!
    echo ========================================
    echo.
    echo Created databases:
    echo  - policy_service
    echo  - claims_service
    echo  - customer_service
    echo.
    echo User: insurance
    echo Password: insurance123
    echo.
    echo Next steps:
    echo 1. Build the project: mvn clean install
    echo 2. Start Service Registry first
    echo 3. Then start each microservice
    echo.
) else (
    echo.
    echo ERROR: Database setup failed
    echo Please check that PostgreSQL is running
    echo.
)

pause
