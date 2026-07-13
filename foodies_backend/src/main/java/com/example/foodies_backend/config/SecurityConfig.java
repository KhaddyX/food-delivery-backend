package com.example.foodies_backend.config;

// Importing necessary classes and annotations for configuration and security
import com.example.foodies_backend.service.AppUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Marking this class as a configuration class for Spring
@Configuration
// Enabling Spring Security for the application
@EnableWebSecurity
// Generating a constructor with all required fields using Lombok
@AllArgsConstructor
public class SecurityConfig {

// Injecting a custom user details service for authentication
private final AppUserDetailsService userDetailsService;
// Injecting a custom JWT authentication filter
private final JwtAuthenticationFilter jwtAuthenticationFilter;

// Defining the security filter chain bean
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf().disable() // Disabling CSRF protection
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuring CORS
            .authorizeHttpRequests(auth -> auth
                    // Permitting access to specific endpoints without authentication
                    .requestMatchers("/api/register", "/api/login", "/api/foods/**", "/api/orders/all", "/api/orders/status/**").permitAll()
                    // Requiring authentication for all other requests
                    .anyRequest().authenticated())
            // Setting session management to stateless for JWT-based authentication
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Adding the JWT authentication filter before the default username-password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    // Building and returning the security filter chain
    return http.build();
}

// Defining a password encoder bean using BCrypt with a strength of 12
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Increased strength for better security
}

// Defining a CORS configuration source bean
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // Allowing specific origins for cross-origin requests
    config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
    // Allowing specific HTTP methods
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    // Allowing specific headers in requests
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    // Allowing credentials in cross-origin requests
    config.setAllowCredentials(true);

    // Registering the CORS configuration for all endpoints
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}

// Defining an authentication manager bean
@Bean
public AuthenticationManager authenticationManager() {
    // Creating a DAO authentication provider
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    // Setting the custom user details service
    authProvider.setUserDetailsService(userDetailsService);
    // Setting the password encoder
    authProvider.setPasswordEncoder(passwordEncoder());
    // Returning an authentication manager with the configured provider
    return new ProviderManager(authProvider);
}
}