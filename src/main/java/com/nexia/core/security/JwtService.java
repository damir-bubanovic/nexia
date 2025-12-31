package com.nexia.core.security;

import com.nexia.core.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long ttlSeconds;

    public JwtService(
            @Value("${nexia.security.jwt.secret}") String secret,
            @Value("${nexia.security.jwt.issuer:nexia}") String issuer,
            @Value("${nexia.security.jwt.ttlSeconds:3600}") long ttlSeconds
    ) {
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalStateException("nexia.security.jwt.secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.trim().getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public long ttlSeconds() {
        return ttlSeconds;
    }

    public String generate(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getId().toString())
                .claims(Map.of(
                        "email", user.getEmail(),
                        "role", user.getRole()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID subjectAsUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }
}
