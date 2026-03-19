package com.revshopproject.revshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.revshopproject.revshop.dto.LoginRequestDTO;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testLogin() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("buyer@test.com");
        dto.setPassword("Valid1@Pass");

        User mockUser = new User();
        mockUser.setEmail("buyer@test.com");

        when(userService.login(dto)).thenReturn(mockUser);

        // Act
        ResponseEntity<User> response = userController.login(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("buyer@test.com", response.getBody().getEmail());
    }
}
