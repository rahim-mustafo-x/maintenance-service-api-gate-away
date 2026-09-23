package org.safa.maintenanceserviceapigateaway.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService{
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extactAllClaims(token);
        return resolver.apply(claims);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            List<?> roles = claims.get("roles", List.class);

            return roles.stream()
                    .map(String::valueOf)
                    .toList();
        });
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extactAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    @Override
    public Claims extactAllClaims(String token) {
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
