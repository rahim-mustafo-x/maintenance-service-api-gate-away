package org.safa.maintenanceserviceapigateaway.jwt.service;

import io.jsonwebtoken.Claims;
import java.util.function.Function;

public interface JwtService {
    <T> T extractClaim(String token, Function<Claims, T> resolver);
    String extractUsername(String token);
    boolean isTokenExpired(String token);
    long extractUserId(String token);
    Claims extactAllClaims(String token);
}