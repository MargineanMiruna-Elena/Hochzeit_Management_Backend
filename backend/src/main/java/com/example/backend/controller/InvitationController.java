package com.example.backend.controller;

import com.example.backend.enums.FoodPreference;
import com.example.backend.enums.TransportationMethod;
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
import java.util.stream.Collectors;

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
        System.out.println("Token length: " + token.length());
        
        try {
            Optional<ParticipantToken> participantToken = participantTokenService.validateToken(token);
            
            if (participantToken.isEmpty()) {
                System.out.println("Token validation failed - token not found or expired");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }
            
            System.out.println("Token validated successfully");
            
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
            response.put("date", dateDisplay);
            response.put("location", "The Grand Ballroom"); 
            response.put("organizer", organizers);
            response.put("image", event.getImageUrl() != null ? event.getImageUrl() : 
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=800");
            
            response.put("askFoodPreferences", event.getAskFoodPreferences());
            response.put("askTransportation", event.getAskTransportation());
            
            if (Boolean.TRUE.equals(event.getAskFoodPreferences())) {
                try {
                    List<Map<String, String>> foodOptions = event.getAvailableFoodPreferences().stream()
                        .map(pref -> {
                            FoodPreference fp = FoodPreference.valueOf(pref.toString());
                            return Map.of("value", fp.name(), "label", fp.getDisplayName());
                        })
                        .collect(Collectors.toList());
                    response.put("availableFoodPreferences", foodOptions);
                    System.out.println("Food preferences: " + foodOptions);
                } catch (Exception e) {
                    System.err.println("Error processing food preferences: " + e.getMessage());
                    e.printStackTrace();
                    response.put("availableFoodPreferences", new ArrayList<>());
                }
            }
            
            if (Boolean.TRUE.equals(event.getAskTransportation())) {
                try {
                    List<Map<String, String>> transportOptions = event.getAvailableTransportationMethods().stream()
                        .map(method -> {
                            TransportationMethod tm = TransportationMethod.valueOf(method.toString());
                            return Map.of("value", tm.name(), "label", tm.getDisplayName());
                        })
                        .collect(Collectors.toList());
                    response.put("availableTransportationMethods", transportOptions);
                    System.out.println("Transportation methods: " + transportOptions);
                } catch (Exception e) {
                    System.err.println("Error processing transportation methods: " + e.getMessage());
                    e.printStackTrace();
                    response.put("availableTransportationMethods", new ArrayList<>());
                }
            }
            
            System.out.println("Successfully returning response");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR in getInvitationDetails: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/rsvp")
    public ResponseEntity<?> submitRsvp(
            @RequestParam String token,
            @RequestBody RsvpRequest rsvpRequest) {
        
        System.out.println("=== RSVP SUBMISSION ===");
        System.out.println("Token: " + token);
        System.out.println("Accept: " + rsvpRequest.getAccept());
        System.out.println("Food prefs: " + rsvpRequest.getFoodPreferences());
        System.out.println("Transportation: " + rsvpRequest.getTransportationMethod());
        
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
                    Set<FoodPreference> selectedFoodPreferences = rsvpRequest.getFoodPreferences().stream()
                        .map(FoodPreference::valueOf)
                        .collect(Collectors.toSet());
                    invitationResponse.setFoodPreferences(selectedFoodPreferences);
                }
                
                if (rsvpRequest.getTransportationMethod() != null && !rsvpRequest.getTransportationMethod().isEmpty()) {
                    TransportationMethod selectedTransportation = TransportationMethod.valueOf(rsvpRequest.getTransportationMethod());
                    invitationResponse.setTransportationMethod(selectedTransportation);
                }
            }
            
            invitationResponseRepository.save(invitationResponse);
            
            System.out.println("RSVP saved successfully");
            
            return ResponseEntity.ok(Map.of(
                "message", "RSVP submitted successfully",
                "attending", rsvpRequest.getAccept()
            ));
            
        } catch (Exception e) {
            System.err.println("ERROR in submitRsvp: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
    
    public static class RsvpRequest {
        private Boolean accept;
        private List<String> foodPreferences;
        private String transportationMethod;
        
        public Boolean getAccept() { return accept; }
        public void setAccept(Boolean accept) { this.accept = accept; }
        
        public List<String> getFoodPreferences() { return foodPreferences; }
        public void setFoodPreferences(List<String> foodPreferences) { 
            this.foodPreferences = foodPreferences; 
        }
        
        public String getTransportationMethod() { return transportationMethod; }
        public void setTransportationMethod(String transportationMethod) { 
            this.transportationMethod = transportationMethod; 
        }
    }
}