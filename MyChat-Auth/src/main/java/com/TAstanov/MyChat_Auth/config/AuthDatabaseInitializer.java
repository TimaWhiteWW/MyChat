package com.TAstanov.MyChat_Auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthDatabaseInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS user_auth (
                    email VARCHAR(100) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    tag VARCHAR(50) NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    email_verified BOOLEAN DEFAULT FALSE
                )
                """);
        jdbcTemplate.execute("""
                ALTER TABLE user_auth
                    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS email_verification_codes (
                    email VARCHAR(100) PRIMARY KEY REFERENCES user_auth(email) ON DELETE CASCADE,
                    code VARCHAR(6) NOT NULL,
                    expires_at TIMESTAMP NOT NULL,
                    created_at TIMESTAMP NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS jwt_access_token (
                    id UUID PRIMARY KEY,
                    access_token TEXT NOT NULL,
                    expiration_date TIMESTAMP
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS jwt_refresh_token (
                    id UUID PRIMARY KEY,
                    refresh_token TEXT NOT NULL,
                    expiration_date TIMESTAMP
                )
                """);
    }
}
