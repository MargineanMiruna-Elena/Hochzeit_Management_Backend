package com.example.backend.controller;

import com.example.backend.dto.LocationResponse;
import com.example.backend.dto.RsvpRequest;
import com.example.backend.enums.FoodPreference;
import com.example.backend.model.*;
import com.example.backend.repository.InvitationResponseRepository;
import com.example.backend.repository.LocationRepository;
import com.example.backend.repository.ParticipantRepository;
import com.example.backend.service.LocationService;
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
    private LocationRepository locationRepository;

    @Autowired
    private InvitationResponseRepository invitationResponseRepository;
    @Autowired
    private LocationService locationService;

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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            String startDate = event.getStartDate().format(formatter);
            String endDate = event.getEndDate().format(formatter);
            String dateDisplay = startDate.equals(endDate) ? startDate : startDate + " - " + endDate;

            String organizers = event.getEmailOrg1();
            if (event.getEmailOrg2() != null && !event.getEmailOrg2().isBlank()) {
                organizers += " & " + event.getEmailOrg2();
            }

            Optional<Location> location = locationRepository.findLocationById(event.getLocationID());
            String displayLocation;
            if (location.isEmpty()) {
                displayLocation = "No location yet.";
            } else {
                displayLocation = location.get().getName() + ", " + location.get().getAddress();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("title", event.getName());
            response.put("description", event.getDescription());
            response.put("image", event.getImageUrl());
            response.put("organizer", organizers);
            response.put("date", dateDisplay);
            response.put("location", displayLocation);

            if (!event.getFoodPreferences().isEmpty()) {
                Set<String> foodPreferences = new HashSet<>();
                for (FoodPreference f: event.getFoodPreferences()) {
                    foodPreferences.add(f.getDisplayName());
                }
                response.put("availableFoodPreferences", foodPreferences);
            }
            response.put("hasParking", event.getHasParking());

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
                    .orElse(new InvitationResponse(event, participant, rsvpRequest.getFoodPreferences(), rsvpRequest.getNeedsParking()));

            if (Boolean.TRUE.equals(rsvpRequest.getAccept())) {
                invitationResponse.setFoodPreferences(rsvpRequest.getFoodPreferences());
                invitationResponse.setNeedsParking(rsvpRequest.getNeedsParking());
            } else {
                invitationResponse.setFoodPreferences(null);
                invitationResponse.setNeedsParking(null);
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
}