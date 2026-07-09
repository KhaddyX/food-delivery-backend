// The `AppUserDetailsService` class implements the `UserDetailsService` interface to provide custom user authentication logic.
// It uses the `UserRepository` to fetch user details from the database and adapts them to Spring Security's `UserDetails` format.

package com.example.foodies_backend.service;

import com.example.foodies_backend.entity.UserEntity; // Represents the user entity in the database.
import com.example.foodies_backend.repository.UserRepository; // Repository for accessing user data.
import lombok.AllArgsConstructor; // Generates a constructor with all required fields.
import org.springframework.security.core.userdetails.User; // Represents a Spring Security user.
import org.springframework.security.core.userdetails.UserDetails; // Interface for user details used by Spring Security.
import org.springframework.security.core.userdetails.UserDetailsService; // Interface for loading user-specific data.
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exception thrown when a user is not found.
import org.springframework.stereotype.Service; // Marks this class as a Spring service.

import java.util.Collections; // Provides an empty list for user authorities.

@Service // Marks this class as a Spring-managed service.
@AllArgsConstructor // Generates a constructor with all required fields.
public class AppUserDetailsService implements UserDetailsService {

// Injecting the UserRepository to fetch user data from the database.
private final UserRepository userRepository;

// Overriding the `loadUserByUsername` method to load user details by email.
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Fetching the user entity from the database using the email.
    UserEntity user = userRepository.findByEmail(email)
            // Throwing an exception if the user is not found.
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    // Returning a Spring Security `User` object with the user's email, password, and no authorities.
    return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
}
}