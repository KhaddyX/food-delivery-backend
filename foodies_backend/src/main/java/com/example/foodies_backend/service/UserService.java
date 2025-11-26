package com.example.foodies_backend.service;

import com.example.foodies_backend.dto.UserRequest;
import com.example.foodies_backend.dto.UserResponse;


public interface UserService {

UserResponse registerUser(UserRequest request);

String findByUserId();
}
