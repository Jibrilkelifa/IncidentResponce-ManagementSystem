package com.example.Incident.services;

import com.example.Incident.model.Incident;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEscalationNotification(String to, Incident incident) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Incident Escalated: " + incident.getId());
        message.setText("Incident Description: " + incident.getDescription() + "\n\nPlease take the necessary action.");

        mailSender.send(message);
    }
}
