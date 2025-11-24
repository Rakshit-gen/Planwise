#!/bin/bash

# Database setup script for PlanWise
# This script creates the database user and database

echo "Setting up PlanWise database..."

# Try to connect to PostgreSQL
# First, try connecting as the default postgres superuser
PGUSER=${PGUSER:-postgres}
PGPASSWORD=${PGPASSWORD:-postgres}
PGHOST=${PGHOST:-localhost}
PGPORT=${PGPORT:-5432}

# Check if we can connect
if psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -c '\q' 2>/dev/null; then
    echo "Connected to PostgreSQL as $PGUSER"
else
    echo "Attempting to connect to PostgreSQL..."
    echo "If this fails, you may need to:"
    echo "1. Start PostgreSQL: docker-compose up -d postgres"
    echo "2. Or connect manually: psql -U <your-postgres-user>"
    read -p "Press Enter to continue or Ctrl+C to cancel..."
fi

# Create user if it doesn't exist
echo "Creating user 'planwise'..."
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -c "CREATE USER planwise WITH PASSWORD 'planwise';" 2>/dev/null || echo "User may already exist, continuing..."

# Create database if it doesn't exist
echo "Creating database 'planwise'..."
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -c "CREATE DATABASE planwise OWNER planwise;" 2>/dev/null || echo "Database may already exist, continuing..."

# Grant privileges
echo "Granting privileges..."
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d planwise -c "GRANT ALL PRIVILEGES ON DATABASE planwise TO planwise;" 2>/dev/null || echo "Privileges may already be set"

echo "Database setup complete!"
echo ""
echo "You can now start the application with:"
echo "  mvn spring-boot:run"
echo ""
echo "Or use docker-compose:"
echo "  docker-compose up -d"

