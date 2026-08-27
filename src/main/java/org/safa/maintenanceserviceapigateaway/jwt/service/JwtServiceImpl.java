package org.safa.maintenanceserviceapigateaway.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extactAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    private Date extractExpiration(String token) {
        //takes the period of time of the token to be expired
        return extractClaim(token, Claims::getExpiration);
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
