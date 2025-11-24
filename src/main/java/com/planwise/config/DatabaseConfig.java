package com.planwise.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuration for Neon PostgreSQL database connection
 * Supports both JDBC URL format and Neon connection string format
 */
@Configuration
public class DatabaseConfig {
    
    /**
     * If DATABASE_URL is provided in Neon format (postgresql://user:pass@host/db),
     * this will be handled automatically by Spring Boot when the URL is properly formatted.
     * 
     * For Neon, ensure your DATABASE_URL is in JDBC format:
     * jdbc:postgresql://host:port/database?sslmode=require
     * 
     * Or use separate properties:
     * DATABASE_HOST, DATABASE_PORT, DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD
     */
}

