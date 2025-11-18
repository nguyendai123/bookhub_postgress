package com.bookhup.service.impl;

import com.bookhup.repository.UserRepository;
import com.bookhup.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    @Async
    public void  sendMailResetPassword(String newPassword, String email) {

        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            String subject = "Reset your password";
            String msgBody = "Hi," +
                    "\n\n You requested to reset the password for your Bookhub account with e-mail address (" +
                    email + ")." +
                    "\nThis is your new password: " +
                    newPassword +
                    "\n\n Thanks" +
                    "\n Bookhub team.";

            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText(msgBody);
            mailMessage.setSubject(subject);

            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
