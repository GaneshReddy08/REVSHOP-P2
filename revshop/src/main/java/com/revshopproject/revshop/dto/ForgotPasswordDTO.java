package com.revshopproject.revshop.dto;

public class ForgotPasswordDTO {
    private String email;
    private Long securityQuestionId;
    private String securityAnswer;
    private String newPassword;

    public ForgotPasswordDTO() {
    }

    public ForgotPasswordDTO(String email, Long securityQuestionId, String securityAnswer, String newPassword) {
        this.email = email;
        this.securityQuestionId = securityQuestionId;
        this.securityAnswer = securityAnswer;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(Long securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
