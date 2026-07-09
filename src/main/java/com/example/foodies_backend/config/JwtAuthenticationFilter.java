package com.example.foodies_backend.config;

// Importing necessary classes for JWT authentication and request filtering
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Marking this class as a Spring component to be managed by the Spring container
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

// Injecting the JwtUtil class for token operations
@Autowired
private JwtUtil jwtUtil;

// Injecting the UserDetailsService to load user details
@Autowired
private UserDetailsService userDetailsService;

// Overriding the doFilterInternal method to handle JWT authentication
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // Retrieving the Authorization header from the request
    final String authHeader = request.getHeader("Authorization");

    // Checking if the header contains a Bearer token
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
        // Extracting the token from the header
        String token = authHeader.substring(7);
        // Extracting the username (email) from the token
        String email = jwtUtil.extractUsername(token);

        // Checking if the username is not null and the user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loading user details using the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Validating the token with the user details
            if (jwtUtil.validateToken(token, userDetails)) {
                // Creating an authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                // Setting additional details for the authentication token
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Setting the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }
    // Proceeding with the filter chain
    filterChain.doFilter(request, response);
}
}