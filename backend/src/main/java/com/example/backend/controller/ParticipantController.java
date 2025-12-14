package com.example.backend.controller;

import com.example.backend.dto.CreateParticipantRequest;
import com.example.backend.dto.ParticipantResponse;
import com.example.backend.dto.UpdateParticipantRequest;
import com.example.backend.service.ParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    // CREATE participant (organizer adds guest)
    @PostMapping("/participants")
    public ResponseEntity<ParticipantResponse> createParticipant(
            @RequestBody CreateParticipantRequest request) {
        ParticipantResponse response = participantService.createParticipant(request);
        return ResponseEntity.status(201).body(response);
    }

    // READ single participant
    @GetMapping("/participants/{id}")
    public ResponseEntity<ParticipantResponse> getParticipant(@PathVariable Long id) {
        ParticipantResponse response = participantService.getParticipant(id);
        return ResponseEntity.ok(response);
    }

    // UPDATE participant
    @PutMapping("/participants/{id}")
    public ResponseEntity<ParticipantResponse> updateParticipant(
            @PathVariable Long id,
            @RequestBody UpdateParticipantRequest request) {
        ParticipantResponse response = participantService.updateParticipant(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE participant
    @DeleteMapping("/participants/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }

    // LIST all participants for an event
    @GetMapping("/events/{eventId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipantsByEvent(
            @PathVariable Long eventId) {
        List<ParticipantResponse> participants = participantService.getParticipantsByEvent(eventId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/events/{eventId}/participants/accepted")
    public ResponseEntity<List<ParticipantResponse>> getAcceptedParticipantsByEvent(
            @PathVariable Long eventId) {
        List<ParticipantResponse> participants = participantService.getAcceptedParticipantsByEvent(eventId);
        return ResponseEntity.ok(participants);
    }

    // Optional: declined participants
    @GetMapping("/events/{eventId}/participants/declined")
    public ResponseEntity<List<ParticipantResponse>> getDeclinedParticipantsByEvent(
            @PathVariable Long eventId) {
        List<ParticipantResponse> participants = participantService.getDeclinedParticipantsByEvent(eventId);
        return ResponseEntity.ok(participants);
    }

    // Optional: pending participants
    @GetMapping("/events/{eventId}/participants/pending")
    public ResponseEntity<List<ParticipantResponse>> getPendingParticipantsByEvent(
            @PathVariable Long eventId) {
        List<ParticipantResponse> participants = participantService.getPendingParticipantsByEvent(eventId);
        return ResponseEntity.ok(participants);
    }
}
