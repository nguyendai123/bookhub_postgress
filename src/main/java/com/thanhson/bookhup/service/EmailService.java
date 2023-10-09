package com.thanhson.bookhup.service;

import com.thanhson.bookhup.exception.ResourceNotFoundException;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public boolean sendMailResetPassword(String newPassword, String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User can not be found with username or email: " + usernameOrEmail));
        // Try block to check for exceptions
        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            String subject = "Reset your password";
            String msgBody = "Hi," +
                    "\n\n You requested to reset the password for your Bookhub account with e-mail address (" +
                    user.getEmail() + ")." +
                    "\nThis is your new password: " +
                    newPassword +
                    "\n\n Thanks" +
                    "\n Bookhub team.";

            mailMessage.setFrom(sender);
            mailMessage.setTo(user.getEmail());
            mailMessage.setText(msgBody);
            mailMessage.setSubject(subject);

            javaMailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
