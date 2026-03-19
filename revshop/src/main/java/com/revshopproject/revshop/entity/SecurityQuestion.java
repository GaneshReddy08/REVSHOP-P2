package com.revshopproject.revshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SECURITY_QUESTIONS")
public class SecurityQuestion {

    @Id
    @Column(name = "QUESTION_ID")
    private Long questionId;

    @Column(name = "QUESTION_TEXT", nullable = false)
    private String questionText;

    // Default Constructor
    public SecurityQuestion() {}

    // Parameterized Constructor
    public SecurityQuestion(Long questionId, String questionText) {
        this.questionId = questionId;
        this.questionText = questionText;
    }

    // Getters and Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
}