package com.bookhup.service;

public interface EmailService {
    void sendMailResetPassword(String newPassword, String usernameOrEmail);
}
