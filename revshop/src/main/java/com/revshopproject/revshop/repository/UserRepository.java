package com.revshopproject.revshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revshopproject.revshop.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom finder for login and registration checks
    Optional<User> findByEmail(String email);
}