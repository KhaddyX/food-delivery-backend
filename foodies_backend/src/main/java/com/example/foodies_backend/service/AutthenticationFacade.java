package com.example.foodies_backend.service;

import org.springframework.security.core.Authentication;

public interface AutthenticationFacade {

Authentication getAuthentication();
}