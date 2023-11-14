package com.example.americanbank.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET_KEY = "SuperLongAndSecureSecretKeyThatIsUsedForJWTGenerationAndValidation2234567890";
    private static final long TOKEN_VALIDITY_IN_MILLISECONDS = 1000 * 60 * 60 * 48; // 48 hours

    public String generateTokenForUser(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String token = generateToken(claims, userDetails.getUsername());

        logger.info("Generated JWT for user '{}'.", userDetails.getUsername());

        return token;
    }

    public Jws<Claims> decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token);
    }

    public String getSignedToken(UserDetails userDetails, List<String> roles) {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", "JWT")
                .setIssuer("YourIssuerHere")
                .setAudience("YourAudienceHere")
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY_IN_MILLISECONDS))
                .claim("rol", roles)
                .compact();
    }

    private String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY_IN_MILLISECONDS))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        logger.info("Extracted username '{}' from JWT: {}", username, token);
        return username;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            // Allow a clock skew of 60 seconds
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .setAllowedClockSkewSeconds(60) // Allow 60 seconds skew
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error("JWT signature mismatch error. Token: {}", token, e);
            throw e; // or rethrow the exception to be handled by the calling method
        }
    }


    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean isExpired = expiration.before(new Date());
        logger.info("Checking JWT expiry for token '{}'. Expiry date: {}. Is expired: {}", token, expiration, isExpired);
        return isExpired;
    }

}
