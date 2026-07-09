package com.example.foodies_backend.service;

import com.example.foodies_backend.dto.UserRequest;
import com.example.foodies_backend.dto.UserResponse;
import com.example.foodies_backend.entity.UserEntity;
import com.example.foodies_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

// The `UserServiceImpl` class implements the `UserService` interface to handle user-related operations.
// It includes methods for registering users, retrieving the logged-in user's ID, and converting between DTOs and entities.

@Service // Marks this class as a Spring-managed service.
@AllArgsConstructor // Generates a constructor with all required fields using Lombok.
public class UserServiceImpl implements UserService {

// Injecting the UserRepository to interact with the database for user-related operations.
private final UserRepository userRepository;

// Injecting the PasswordEncoder to securely hash user passwords.
private final PasswordEncoder passwordEncoder;

// Injecting the AuthenticationFacade to retrieve authentication details of the logged-in user.
private final AutthenticationFacade authenticationFacade;

private final EmailService emailService;

// Registers a new user by saving their details in the database.
@Override
public UserResponse registerUser(UserRequest request) {
    // Converts the UserRequest DTO to a UserEntity.
    UserEntity newUser = convertToEntity(request);

    // Saves the new user entity to the database.
    newUser = userRepository.save(newUser);

    // Converts the saved UserEntity to a UserResponse DTO and returns it.
    return convertToResponse(newUser);
}

// Retrieves the ID of the currently logged-in user.
@Override
public String findByUserId() {
    // Retrieves the email of the logged-in user from the authentication context.
    String loggedInUserEmail = authenticationFacade.getAuthentication().getName();

    // Fetches the user entity from the database using the email or throws an exception if not found.
    UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Returns the ID of the logged-in user.
    return loggedInUser.getId();
}

// Converts a UserRequest DTO to a UserEntity.
private UserEntity convertToEntity(UserRequest request) {
    return UserEntity.builder()
            .email(request.getEmail()) // Sets the email.
            .password(passwordEncoder.encode(request.getPassword())) // Encodes the password.
            .name(request.getName()) // Sets the name.
            .build();
}

// Converts a UserEntity to a UserResponse DTO.
private UserResponse convertToResponse(UserEntity registeredUser) {
    return UserResponse.builder()
            .id(registeredUser.getId()) // Sets the user ID.
            .name(registeredUser.getName()) // Sets the name.
            .email(registeredUser.getEmail()) // Sets the email.
            .build();
}
@Override
public void sendResetEmail(String email) {
    UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String token = UUID.randomUUID().toString();

    user.setResetToken(token);
    user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

    userRepository.save(user);

    emailService.sendPasswordResetEmail(user.getEmail(), token);
}

@Override
public void resetPassword(String token, String newPassword) {
    UserEntity user = userRepository.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid reset token"));

    if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
        throw new RuntimeException("Reset token expired");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    user.setResetToken(null);
    user.setResetTokenExpiry(null);

    userRepository.save(user);
}

}