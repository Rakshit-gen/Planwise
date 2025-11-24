package com.planwise.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${app.jwt.secret:}")
    private String secretKey;
    
    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;
    
    @PostConstruct
    public void validateSecret() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "JWT_SECRET environment variable is not set or is empty. " +
                "Please set JWT_SECRET in your Render environment variables. " +
                "Current value: '" + secretKey + "' " +
                "Generate a secret using: openssl rand -base64 32"
            );
        }
        
        // Trim whitespace that might have been accidentally added
        secretKey = secretKey.trim();
        
        try {
            // Validate that the secret is valid Base64
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            
            // HMAC-SHA256 requires at least 32 bytes (256 bits)
            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                    "JWT_SECRET must be at least 32 bytes (256 bits) when decoded. " +
                    "Current decoded length: " + keyBytes.length + " bytes. " +
                    "Generate a new secret using: openssl rand -base64 32"
                );
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                "JWT_SECRET is not a valid Base64 string. " +
                "Error: " + e.getMessage() + " " +
                "Please generate a new secret using: openssl rand -base64 32"
            );
        }
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

