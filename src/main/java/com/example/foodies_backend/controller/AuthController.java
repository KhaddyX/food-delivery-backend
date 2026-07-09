package com.example.foodies_backend.controller;

// Importing necessary classes for authentication and JWT token generation
import com.example.foodies_backend.config.JwtUtil;
import com.example.foodies_backend.dto.AuthenticationRequest;
import com.example.foodies_backend.dto.AuthenticationResponse;
import com.example.foodies_backend.service.AppUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Marking this class as a REST controller and mapping it to the "/api" endpoint
@RestController
@RequestMapping("/api")
// Generating a constructor with all required fields using Lombok
@AllArgsConstructor
public class AuthController {

// Injecting the authentication manager for user authentication
private final AuthenticationManager authenticationManager;
// Injecting the custom user details service to load user details
private final AppUserDetailsService userDetailsService;
// Injecting the JwtUtil class for generating JWT tokens
private final JwtUtil jwtUtil;

// Defining a POST endpoint for user login
@PostMapping("/login")
public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {
    // Authenticating the user using the provided email and password
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    // Loading user details using the email
    final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
    // Generating a JWT token for the authenticated user
    final String jwtToken = jwtUtil.generateToken(userDetails);
    // Returning the email and generated JWT token in the response
    return new AuthenticationResponse(request.getEmail(), jwtToken);
}
}