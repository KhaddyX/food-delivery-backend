// Importing necessary classes for handling HTTP requests and responses
package com.example.foodies_backend.controller;

import com.example.foodies_backend.dto.UserRequest;
import com.example.foodies_backend.dto.UserResponse;
import com.example.foodies_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Marking this class as a REST controller and mapping it to the "/api" endpoint
@RestController
@RequestMapping("/api")
// Generating a constructor with all required fields using Lombok
@AllArgsConstructor
public class UserController {

// Injecting the UserService to handle user-related operations
private final UserService userService;

// Defining a POST endpoint to register a new user
@PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED) // Setting the response status to CREATED
public UserResponse register(@RequestBody UserRequest request) {
    // Delegating the register-user operation to the UserService
    return userService.registerUser(request);
}
@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(@RequestParam String email) {
    userService.sendResetEmail(email);
    return ResponseEntity.ok("Password reset email sent.");
}

@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@RequestParam String token,
                                       @RequestParam String newPassword) {
    userService.resetPassword(token, newPassword);
    return ResponseEntity.ok("Password reset successful.");
}

}