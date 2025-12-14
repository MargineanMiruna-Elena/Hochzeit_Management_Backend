package com.example.backend.controller;

import com.example.backend.model.Event;
import com.example.backend.model.InvitationResponse;
import com.example.backend.model.Participant;
import com.example.backend.model.ParticipantToken;
import com.example.backend.repository.InvitationResponseRepository;
import com.example.backend.repository.ParticipantRepository;
import com.example.backend.service.ParticipantTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/invitation")
public class InvitationController {

    @Autowired
    private ParticipantTokenService participantTokenService;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private InvitationResponseRepository invitationResponseRepository;

    @GetMapping
public ResponseEntity<?> getInvitationDetails(@RequestParam String token) {
    System.out.println("=== INVITATION CONTROLLER ===");
    System.out.println("Received token: " + token);
    
    try {
        Optional<ParticipantToken> participantToken = participantTokenService.validateToken(token);
        
        if (participantToken.isEmpty()) {
            System.out.println("Token validation failed");
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
        }
        
        Event event = participantToken.get().getEvent();
        Participant participant = participantToken.get().getParticipant();
        
        System.out.println("Event: " + event.getName());
        System.out.println("Participant: " + participant.getName());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String startDate = event.getStartDate().format(formatter);
        String endDate = event.getEndDate().format(formatter);
        String dateDisplay = startDate.equals(endDate) ? startDate : startDate + " - " + endDate;
        
        String organizers = event.getEmailOrg1();
        if (event.getEmailOrg2() != null && !event.getEmailOrg2().isBlank()) {
            organizers += " & " + event.getEmailOrg2();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("title", event.getName());
        response.put("description", event.getDescription());
        response.put("image", event.getImageUrl());
        response.put("organizer", organizers); 
        response.put("date", dateDisplay); 
        response.put("location", "Location " + event.getLocationID());
        response.put("askFoodPreferences", event.getAskFoodPreferences());

        if (Boolean.TRUE.equals(event.getAskFoodPreferences())) {
            List<Map<String, String>> foodOptions = Arrays.asList(
                Map.of("value", "VEGETARIAN", "label", "Vegetarian"),
                Map.of("value", "VEGAN", "label", "Vegan"),
                Map.of("value", "NO_PREFERENCE", "label", "No Preference")
            );
            response.put("availableFoodPreferences", foodOptions);
        }

        System.out.println("Returning response: " + response);
        return ResponseEntity.ok(response);
        
    } catch (Exception e) { 
        System.err.println("ERROR in getInvitationDetails: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
}

    @PostMapping("/rsvp")
    public ResponseEntity<?> submitRsvp(
            @RequestParam String token,
            @RequestBody RsvpRequest rsvpRequest) {
        
        System.out.println("=== RSVP SUBMISSION ===");
        System.out.println("Accept: " + rsvpRequest.getAccept());
        System.out.println("Food preferences: " + rsvpRequest.getFoodPreferences());
        
        try {
            Optional<ParticipantToken> participantToken = participantTokenService.validateToken(token);
            
            if (participantToken.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }
            
            Event event = participantToken.get().getEvent();
            Participant participant = participantToken.get().getParticipant();
            
            participant.setAttending(rsvpRequest.getAccept());
            participantRepository.save(participant);
            
            InvitationResponse invitationResponse = invitationResponseRepository
                .findByEventIdAndParticipantId(event.getId(), participant.getId())
                .orElse(new InvitationResponse(event, participant));
            
            if (Boolean.TRUE.equals(rsvpRequest.getAccept())) {
                if (rsvpRequest.getFoodPreferences() != null && !rsvpRequest.getFoodPreferences().isEmpty()) {
                    String foodPrefsStr = String.join(",", rsvpRequest.getFoodPreferences());
                    invitationResponse.setFoodPreferences(foodPrefsStr);
                }
            } else {
                invitationResponse.setFoodPreferences(null);
            }
            
            invitationResponseRepository.save(invitationResponse);
            
            System.out.println("RSVP saved successfully");
            System.out.println("Participant attending: " + participant.getAttending());
            System.out.println("Food preferences: " + invitationResponse.getFoodPreferences());
            
            return ResponseEntity.ok(Map.of(
                "message", "RSVP submitted successfully",
                "attending", participant.getAttending()
            ));
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    public static class RsvpRequest {
        private Boolean accept;
        private List<String> foodPreferences;
        
        public Boolean getAccept() { 
            return accept; 
        }
        
        public void setAccept(Boolean accept) { 
            this.accept = accept; 
        }
        
        public List<String> getFoodPreferences() { 
            return foodPreferences; 
        }
        
        public void setFoodPreferences(List<String> foodPreferences) { 
            this.foodPreferences = foodPreferences; 
        }
    }
}