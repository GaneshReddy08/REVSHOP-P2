package com.revshopproject.revshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revshopproject.revshop.dto.ChangePasswordDTO;
import com.revshopproject.revshop.dto.ForgotPasswordDTO;
import com.revshopproject.revshop.dto.LoginRequestDTO;
import com.revshopproject.revshop.dto.UserRegistrationDTO;
import com.revshopproject.revshop.entity.SecurityQuestion;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.service.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST: http://localhost:8888/api/users/register
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDTO dto) {
        logger.info("Received registration request for email: {}", dto.getEmail());
        // Map DTO to Entity
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setMobileNumber(dto.getMobileNumber());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setBusinessName(dto.getBusinessName());
        user.setAddress(dto.getAddress());
        user.setSecurityAnswer(dto.getSecurityAnswer());

        // Handle nested SecurityQuestion object mapping for the Service logic
        if (dto.getSecurityQuestionId() != null) {
            SecurityQuestion sq = new SecurityQuestion();
            sq.setQuestionId(dto.getSecurityQuestionId());
            user.setSecurityQuestion(sq);
        }

        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // GET: http://localhost:8888/api/users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    // POST: http://localhost:8888/api/users/login
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequestDTO dto) {
        logger.info("Received login request for email: {}", dto.getEmail());
        User user = userService.login(dto);
        logger.debug("Login successful for user email: {}", user.getEmail());
        return ResponseEntity.ok(user);
    }

    // POST: http://localhost:8888/api/users/change-password
    @PostMapping("/change-password")
    public ResponseEntity<User> changePassword(@RequestBody ChangePasswordDTO dto) {
        logger.info("Received change password request for email: {}", dto.getEmail());
        User user = userService.changePassword(dto);
        return ResponseEntity.ok(user);
    }

 // Used from dashboard (logged-in user — no security question needed)
    // POST: /api/users/change-password-authenticated
    // Body: { "oldPassword": "...", "newPassword": "..." }
    @PostMapping("/change-password-authenticated")
    public ResponseEntity<?> changePasswordAuthenticated(
            @RequestBody Map<String, String> body) {
        try {
            User currentUser = userService.getCurrentUser();
            ChangePasswordDTO dto = new ChangePasswordDTO();
            dto.setEmail(currentUser.getEmail());      // email from session — user doesn't type it
            dto.setOldPassword(body.get("oldPassword"));
            dto.setNewPassword(body.get("newPassword"));
            userService.changePassword(dto);
            logger.info("Password changed successfully for authenticated user email: {}", currentUser.getEmail());
            return ResponseEntity.ok(Map.of("message", "Password changed successfully!"));
        } catch (RuntimeException e) {
            logger.warn("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    
    // GET: http://localhost:8888/api/users/security-question?email=...
    @GetMapping("/security-question")
    public ResponseEntity<?> getSecurityQuestion(@RequestParam String email) {
        try {
            logger.info("Fetching security question for email: {}", email);
            return userService.getUserByEmail(email)
                    .map(user -> {
                        if (user.getSecurityQuestion() == null) {
                            return ResponseEntity.badRequest()
                                    .<Object>body(Map.of("message", "No security question set for this account."));
                        }
                        return ResponseEntity.ok(Map.of(
                                "questionId", user.getSecurityQuestion().getQuestionId(),
                                "questionText", user.getSecurityQuestion().getQuestionText()
                        ));
                    })
                    .orElse(ResponseEntity.badRequest()
                            .body(Map.of("message", "No account found with that email address.")));
        } catch (Exception ex) {
            logger.warn("Failed to fetch security question for email: {} - {}", email, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    // POST: http://localhost:8888/api/users/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO dto) {
        try {
            if (dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
                logger.warn("Forgot password attempt with empty new password for email: {}", dto.getEmail());
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "New password cannot be empty."));
            }
            User user = userService.forgotPassword(dto);
            logger.info("Password reset successful for email: {}", dto.getEmail());
            return ResponseEntity.ok(user);
        } catch (RuntimeException ex) {
            logger.warn("Password reset failed for email: {} - Reason: {}", dto.getEmail(), ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}