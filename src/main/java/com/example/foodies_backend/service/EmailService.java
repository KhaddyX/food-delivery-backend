package com.example.foodies_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

private final JavaMailSender mailSender;

public void sendPasswordResetEmail(String to, String token) {
	String resetUrl = "http://localhost:5173/reset-password?token=" + token;

	SimpleMailMessage message = new SimpleMailMessage();
	message.setTo(to);
	message.setSubject("Password Reset Request");
	message.setText("Click the link to reset your password:\n" + resetUrl);

	mailSender.send(message);
}
}
