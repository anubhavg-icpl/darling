package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret:SuperSecretRickPrimeDimensionalKeyOmegaDevice999999}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms:86400000}") // 24 hours by default
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}") // 7 days by default
    private long refreshTokenExpirationMs;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId())
                .withClaim("email", user.getEmail())
                .withClaim("name", user.getName())
                .withClaim("role", user.getRole() != null ? user.getRole().name() : "ROLE_USER")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .sign(getAlgorithm());
    }

    public String generateRefreshToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId())
                .withClaim("type", "REFRESH")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .sign(getAlgorithm());
    }

    public DecodedJWT validateAndDecodeToken(String token) {
        try {
            return JWT.require(getAlgorithm())
                    .build()
                    .verify(token);
        } catch (TokenExpiredException e) {
            throw new com.example.backend.exception.TokenExpiredException("The JWT token has expired");
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException("Invalid JWT signature or token");
        }
    }

    public String extractEmail(String token) {
        return validateAndDecodeToken(token).getSubject();
    }

    public Long extractUserId(String token) {
        return validateAndDecodeToken(token).getClaim("userId").asLong();
    }

    public String extractRole(String token) {
        return validateAndDecodeToken(token).getClaim("role").asString();
    }
}
