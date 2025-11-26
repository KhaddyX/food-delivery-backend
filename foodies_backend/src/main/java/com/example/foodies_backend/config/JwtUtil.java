package com.example.foodies_backend.config;

// Importing necessary classes for JWT operations and Spring components
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Marking this class as a Spring component to be managed by the Spring container
@Component
public class JwtUtil {

// Injecting the secret key for JWT signing from application properties
@Value("${jwt.secret.key}")
private String SECRET_KEY;

// Method to generate the signing key using the secret key
private Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // Using HMAC-SHA for signing
}

// Method to generate a JWT token for a given user
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>(); // Initializing claims (empty in this case)
    return createToken(claims, userDetails.getUsername()); // Creating the token with claims and username
}

// Method to create a JWT token with claims and subject (username)
private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
            .setClaims(claims) // Setting claims in the token
            .setSubject(subject) // Setting the subject (username)
            .setIssuedAt(new Date(System.currentTimeMillis())) // Setting the issue date
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10)) // Setting expiration (10 days)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signing the token with the secret key
            .compact(); // Compacting the token into a string
}

// Method to extract the username (subject) from a token
public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject); // Extracting the subject claim
}

// Method to extract the expiration date from a token
public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration); // Extracting the expiration claim
}

// Generic method to extract a specific claim from a token
public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token); // Extracting all claims
    return claimsResolver.apply(claims); // Resolving the specific claim
}

// Method to extract all claims from a token
private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey()) // Setting the signing key for validation
            .build()
            .parseClaimsJws(token) // Parsing the token
            .getBody(); // Retrieving the claims body
}

// Method to check if a token is expired
private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date()); // Comparing expiration date with the current date
}

// Method to validate a token against user details
public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token); // Extracting the username from the token
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Validating username and expiration
}
}