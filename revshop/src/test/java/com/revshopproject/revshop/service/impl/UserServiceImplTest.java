package com.revshopproject.revshop.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.revshopproject.revshop.dto.LoginRequestDTO;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.CartRepository;
import com.revshopproject.revshop.repository.SecurityQuestionRepository;
import com.revshopproject.revshop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private SecurityQuestionRepository questionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
    }

    @Test
    void testLogin_Success() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("rawPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);

        User result = userService.login(dto);
        
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(dto));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(dto));
        assertTrue(exception.getMessage().contains("Invalid email or password"));
    }
}
