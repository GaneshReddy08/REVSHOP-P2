package com.revshopproject.revshop.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revshopproject.revshop.dto.ChangePasswordDTO;
import com.revshopproject.revshop.dto.ForgotPasswordDTO;
import com.revshopproject.revshop.dto.LoginRequestDTO;
import com.revshopproject.revshop.entity.Cart;
import com.revshopproject.revshop.entity.SecurityQuestion;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.CartRepository;
import com.revshopproject.revshop.repository.SecurityQuestionRepository;
import com.revshopproject.revshop.repository.UserRepository;
import com.revshopproject.revshop.service.UserService;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final SecurityQuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, 
                           CartRepository cartRepository, 
                           SecurityQuestionRepository questionRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        logger.debug("Attempting to register new user with email: {}", user.getEmail());
        // Validation: Name must not contain numbers
        if (user.getName() == null || !user.getName().matches("^[a-zA-Z\\s]+$")) {
            logger.error("User registration failed: Name validation error.");
            throw new RuntimeException("Name must not contain numbers or special characters.");
        }

        // Validation: Valid email format
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$")) {
            throw new RuntimeException("Invalid email format. Must be a valid domain (e.g., .com, .org).");
        }

        // Validation: Strong Password
        if (user.getPassword() == null || !user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_!]).{8,}$")) {
            throw new RuntimeException("Password must be at least 8 characters long, and include a mix of uppercase, lowercase, digits, and symbols.");
        }

        // Validation: Mobile number exactly 10 digits and starts with 6-9
        if (user.getMobileNumber() == null || !String.valueOf(user.getMobileNumber()).matches("^[6-9][0-9]{9}$")) {
            throw new RuntimeException("Mobile number must be exactly 10 digits and start with 6, 7, 8, or 9.");
        }

        // Validation: Seller must have a Business Name
        if ("SELLER".equals(user.getRole()) && (user.getBusinessName() == null || user.getBusinessName().trim().isEmpty())) {
            throw new RuntimeException("Business Name is required for Sellers.");
        }

        // Validation: Prevent duplicate emails
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Registration rejected: Email already registered: {}", user.getEmail());
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }

        // 2. Fix Transient Error: Fetch the managed SecurityQuestion from DB
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().getQuestionId() == null) {
            throw new RuntimeException("Security Question ID is required for registration.");
        }
        
        SecurityQuestion managedQuestion = questionRepository.findById(user.getSecurityQuestion().getQuestionId())
                .orElseThrow(() -> new RuntimeException("Security Question not found with ID: " 
                        + user.getSecurityQuestion().getQuestionId()));
        user.setSecurityQuestion(managedQuestion);

        // 3. Security: Hash the password before it hits Oracle
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSecurityAnswer(passwordEncoder.encode(user.getSecurityAnswer()));

        // 4. Persistence: Save User first
        User savedUser = userRepository.save(user);

        // 5. Automation: Every user gets a cart immediately
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        logger.info("Successfully registered user with email: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User login(LoginRequestDTO loginRequest) {
        logger.debug("Attempting login for email: {}", loginRequest.getEmail());
        // 1. Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequest.getEmail()));

        // 2. Compare raw input with hashed DB password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed for email: {}. Incorrect password.", loginRequest.getEmail());
            throw new RuntimeException("Invalid email or password");
        }

        logger.info("Subject successfully authenticated: {}", user.getEmail());
        return user;
    }

    @Override
    @Transactional
    public User changePassword(ChangePasswordDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getEmail()));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }
        
        // Hash and save new password
        // Check if new password is the same as the current password
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from the current password.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User forgotPassword(ForgotPasswordDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getEmail()));

        // Verify the security question ID matches the one stored for this user
        if (user.getSecurityQuestion() == null
                || dto.getSecurityQuestionId() == null
                || !user.getSecurityQuestion().getQuestionId().equals(dto.getSecurityQuestionId())) {
            throw new RuntimeException("Security Question does not match records.");
        }

        // Verify the security answer (stored as BCrypt hash — must use matches())
        if (dto.getSecurityAnswer() == null || dto.getSecurityAnswer().trim().isEmpty()) {
            throw new RuntimeException("Security Answer cannot be empty.");
        }
        if (!passwordEncoder.matches(dto.getSecurityAnswer().trim(), user.getSecurityAnswer())) {
            // Fallback for older plaintext security answers
            if (!dto.getSecurityAnswer().trim().equalsIgnoreCase(user.getSecurityAnswer())) {
                throw new RuntimeException("Incorrect Security Answer.");
            }
        }

        // Validate new password strength (same rules as registration)
        if (dto.getNewPassword() == null
                || !dto.getNewPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_!]).{8,}$")) {
            throw new RuntimeException(
                "Password must be at least 8 characters long, and include a mix of uppercase, lowercase, digits, and symbols.");
        }

        // New password must differ from the current password
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from the current password.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        logger.info("Password successfully reset via forgot-password for email: {}", dto.getEmail());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("No authenticated user found in SecurityContext.");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found in database for current authentication."));
    }
}