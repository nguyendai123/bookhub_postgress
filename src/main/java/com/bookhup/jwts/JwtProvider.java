package com.bookhup.jwts;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${bookhub.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bookhub.app.jwtExpirationMs}")
    private int jwtExpirationMs;


    private final String SECRET = "BOOKHUB_SECRET_KEY";

    public String generateToken(Long userId, Set<String> roles, Set<String> permissions) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))  // userId
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // 1 day
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(cleanToken(token));
        return Long.valueOf(claims.getSubject());
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Claims claims = extractAllClaims(cleanToken(token));
        return new HashSet<>((List<String>) claims.get("roles"));
    }
    public Set<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);

        Object rawPermissions = claims.get("permissions");

        if (rawPermissions == null) {
            return Collections.emptySet();
        }

        if (rawPermissions instanceof List<?>) {
            return ((List<?>) rawPermissions).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        // fallback nếu claim lưu dạng chuỗi "A,B,C"
        return Arrays.stream(rawPermissions.toString().split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    private String cleanToken(String token) {
        if (token.startsWith("Bearer "))
            return token.substring(7);
        return token;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)   // secretKey: byte[] hoặc Key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
