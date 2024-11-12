package com.example.Incident.services;

import com.example.Incident.model.Incident;
import com.example.Incident.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class NotificationService {
    @Value("${sms.api.url}")
    private String smsApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private JavaMailSender mailSender;
    public void sendEscalationEmail(String toEmail, String subject, Incident incident) {
        try {
            // Create a detailed message body
            String emailBody = String.format(
                    "Incident ID: %d\nEscalated To: %s\nDescription: %s\nStatus: %s\nSeverity: %s\n" +
                            "Affected System: %s\nEscalated By: %s\nSource: %s\nCreated At: %s\n\n" +
                            "Please review and take necessary action.",
                    incident.getId(),
                    incident.getEscalatedTo(),
                    incident.getDescription(),
                    incident.getStatus(),
                    incident.getSeverity(),
                    incident.getAffectedSystem(),
                    incident.getEscalatedBy(),
                    incident.getSource(),
                    incident.getCreatedAt()
            );

            // Set email message details
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Incident.Response@coopbankoromiasc.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(emailBody);

            // Send email
            mailSender.send(message);
            System.out.println("Escalation email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send escalation email to " + toEmail + ": " + e.getMessage());
        }
    }
    public void sendResetPasswordEmail(String toEmail, String subject, String resetToken) {
        try {
            // Create a detailed message body including the reset token
            String emailBody = String.format(
                    "You have requested to reset your password. Please use the following token to reset your password:\n\n" +
                            "Reset Token: %s\n\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Thanks,\nIncident Response Management System",
                    resetToken
            );

            // Set email message details
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Incident.Response@coopbankoromiasc.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(emailBody);

            // Send email
            mailSender.send(message);
            System.out.println("Reset password token sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send reset password token to " + toEmail + ": " + e.getMessage());
        }
    }


//    public void sendEscalationNotification(Incident incident, String escalatedTo) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(escalatedTo);
//        message.setSubject("Incident Escalation Notification: ID " + incident.getId());
//        message.setText("The following incident has been escalated:\n\n" +
//                "Incident ID: " + incident.getId() + "\n" +
//                "Description: " + incident.getDescription() + "\n" +
//                "Status: " + incident.getStatus() + "\n\n" +
//                "Please review the incident as soon as possible.");
//
//        mailSender.send(message);
//    }

    public void sendEscalationSms(String phoneNumber, Incident incident) {
        try {
            // Construct a concise SMS message with critical details
            String smsMessage = String.format(
                    "Incident ID: %d\nEscalated To: %s\nDescription: %s\nStatus: %s\nSeverity: %s\n" +
                            "Please check the incident details for further action.",
                    incident.getId(),
                    incident.getEscalatedTo(),
                    incident.getDescription(),
                    incident.getStatus(),
                    incident.getSeverity()
            );

            // Encode and send SMS message
            String encodedPhoneNumber = URLEncoder.encode(phoneNumber, StandardCharsets.UTF_8.toString());
            String encodedMessage = URLEncoder.encode(smsMessage, StandardCharsets.UTF_8.toString());

            // Construct URI for the SMS API
            URI uri = new URI(smsApiUrl + "&to=" + encodedPhoneNumber + "&text=" + encodedMessage);

            // Send GET request to the SMS API
            restTemplate.getForObject(uri, String.class);
            System.out.println("SMS sent successfully to " + phoneNumber);
        } catch (Exception e) {
            System.err.println("Failed to send SMS to " + phoneNumber + ": " + e.getMessage());
        }
    }

}