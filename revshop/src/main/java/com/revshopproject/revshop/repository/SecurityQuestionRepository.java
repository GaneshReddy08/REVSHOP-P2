package com.revshopproject.revshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revshopproject.revshop.entity.SecurityQuestion;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {

}
