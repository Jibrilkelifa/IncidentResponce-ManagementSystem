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

    public void sendEscalationNotification(Incident incident, String escalatedTo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(escalatedTo);
        message.setSubject("Incident Escalation Notification: ID " + incident.getId());
        message.setText("The following incident has been escalated:\n\n" +
                "Incident ID: " + incident.getId() + "\n" +
                "Description: " + incident.getDescription() + "\n" +
                "Status: " + incident.getStatus() + "\n\n" +
                "Please review the incident as soon as possible.");

        mailSender.send(message);
    }
}
