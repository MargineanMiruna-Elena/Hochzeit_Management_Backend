package com.example.backend.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.example.backend.model.Event;
import com.example.backend.model.InvitationResponse;
import com.example.backend.model.Participant;
import com.example.backend.repository.InvitationResponseRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class InvitationService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private InvitationResponseRepository invitationResponseRepository;
    
    @Autowired
    private ParticipantTokenService participantTokenService;

    public InvitationService(JavaMailSender mailSender, ParticipantTokenService participantTokenService) {
        this.mailSender = mailSender;
        this.participantTokenService = participantTokenService;
    }

    private String loadEmailTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/email/event-invitation.html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public void sendInvitations(Event event, List<Participant> participants) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dates = event.getStartDate().format(formatter) + " - " + event.getEndDate().format(formatter);
        
        try {
            String htmlTemplate = loadEmailTemplate();
            
            for (Participant p : participants) {
                try {
                    // Create invitation response entry if it doesn't exist
                    if (!invitationResponseRepository.existsByEventIdAndParticipantId(event.getId(), p.getId())) {
                        InvitationResponse invitationResponse = new InvitationResponse(event, p, null, null);
                        invitationResponseRepository.save(invitationResponse);
                    }
                    
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    
                    helper.setTo(p.getEmail());
                    helper.setFrom("eventss978@gmail.com");
                    helper.setSubject("Invitation: " + event.getName());
                    
                    String organizers = event.getEmailOrg1();
                    if (event.getEmailOrg2() != null && !event.getEmailOrg2().isBlank()) {
                        organizers += ", " + event.getEmailOrg2();
                    }
                    
                    String token = participantTokenService.generateTokenForParticipant(event, p);
                    
                    String htmlContent = htmlTemplate
                        .replace("{PARTICIPANT_NAME}", p.getName() != null ? p.getName() : "Guest")
                        .replace("{EVENT_NAME}", event.getName())
                        .replace("{EVENT_DATES}", dates)
                        .replace("{EVENT_DESCRIPTION}", event.getDescription() != null ? event.getDescription() : "No description provided")
                        .replace("{ORGANIZERS}", organizers)
                        .replace("{INVITATION_LINK}", "http://localhost:3000/invitation?token=" + token);
                    
                    helper.setText(htmlContent, true);
                    mailSender.send(message);
                    
                    System.out.println("Invitation sent to: " + p.getEmail());
                    System.out.println("Direct link: http://localhost:3000/invitation?token=" + token);
                    
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