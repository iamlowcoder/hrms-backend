package com.yourcompany.hrms.jwt;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-seconds:86400}")
    private Long expirationSeconds;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Bean
    public Long jwtExpirationSeconds() {
        return expirationSeconds;
    }
}

