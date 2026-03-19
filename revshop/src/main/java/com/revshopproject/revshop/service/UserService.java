package com.revshopproject.revshop.service;

import java.util.List;
import java.util.Optional;

import com.revshopproject.revshop.dto.ChangePasswordDTO;
import com.revshopproject.revshop.dto.ForgotPasswordDTO;
import com.revshopproject.revshop.dto.LoginRequestDTO;
import com.revshopproject.revshop.entity.User;

public interface UserService {
    User registerUser(User user);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    User getUserById(Long id);
    User login(LoginRequestDTO loginRequest);
    User changePassword(ChangePasswordDTO dto);
    User forgotPassword(ForgotPasswordDTO dto);
    User getCurrentUser();
}