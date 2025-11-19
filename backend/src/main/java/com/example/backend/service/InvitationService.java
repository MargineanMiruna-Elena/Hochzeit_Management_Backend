package com.example.backend.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.example.backend.model.Event;
import com.example.backend.model.Participant;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class InvitationService {
    
    @Autowired
    private JavaMailSender mailSender;

    public InvitationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private String loadEmailTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/email/event-invitation.html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public void sendInvitations(Event event, List<Participant> participants) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String dates = df.format(event.getStartDate()) + " - " + df.format(event.getEndDate());
    
    try {
        String htmlTemplate = loadEmailTemplate();
        
        for (Participant p : participants) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                helper.setTo(p.getEmail());
                helper.setFrom("eventss978@gmail.com");
                helper.setSubject("Invitation: " + event.getName());
                
                String organizers = event.getEmailOrg1();
                if (event.getEmailOrg2() != null && !event.getEmailOrg2().isBlank()) {
                    organizers += ", " + event.getEmailOrg2();
                }
                
                String htmlContent = htmlTemplate
                    .replace("{PARTICIPANT_NAME}", p.getName() != null ? p.getName() : "Guest")
                    .replace("{EVENT_NAME}", event.getName())
                    .replace("{EVENT_DATES}", dates)
                    .replace("{EVENT_DESCRIPTION}", event.getDescription() != null ? event.getDescription() : "No description provided")
                    .replace("{ORGANIZERS}", organizers)
                    .replace("{INVITATION_LINK}", "https://yourwebsite.com/invitation/" + event.getId() + "?participant=" + p.getId());
                
                helper.setText(htmlContent, true);
                mailSender.send(message);
                
            } catch (MessagingException e) {
                System.err.println("Failed to send email to " + p.getEmail() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    } catch (IOException e) {
        System.err.println("Failed to load email template: " + e.getMessage());
        e.printStackTrace();
    }
}
}