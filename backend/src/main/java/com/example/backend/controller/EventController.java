package com.example.backend.controller;

import com.example.backend.dto.CreateEventRequest;
import com.example.backend.dto.UpdateEventRequest;
import com.example.backend.dto.EventResponse;
import com.example.backend.model.Event;
import com.example.backend.model.Participant;
import com.example.backend.repository.EventRepository;
import com.example.backend.repository.InvitationResponseRepository;
import com.example.backend.repository.ParticipantRepository;
import com.example.backend.service.EventService;
import com.example.backend.service.InvitationService;
import com.example.backend.service.LocationService;
import com.example.backend.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.backend.repository.UserRepository;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationService invitationService;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CreateEventRequest request) {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            EventResponse response = eventService.createEvent(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        try {
            List<EventResponse> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            EventResponse response = eventService.getEventById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-events")
    public ResponseEntity<?> getMyEvents() {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            List<EventResponse> events = eventService.getEventsByOrganizer(userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/participants-events")
    public ResponseEntity<?> getEventsByParticipant() {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            List<EventResponse> events = eventService.getEventsByParticipant(userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<?> getEventDetail(@PathVariable Long id) {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            EventResponse response = eventService.getOrganizerEventById(userId, id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            BindingResult bindingResult) {
        // Check validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", errors));
        }

        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            EventResponse response = eventService.updateEvent(userId, id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            eventService.deleteEvent(userId, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{eventId}/send-invitations")
    public ResponseEntity<String> sendInvitationsToAll(@PathVariable Long eventId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

            List<Participant> participants = participantRepository.findByEventId(eventId);

            if (participants.isEmpty()) {
                return ResponseEntity.badRequest().body("No participants found for this event.");
            }

            invitationService.sendInvitations(event, participants);

            return ResponseEntity.ok("Invitations sent successfully to " + participants.size() + " participants.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send invitations: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/photos")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long eventId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            // Upload photo via EventService
            String filePath = eventService.uploadPhotoToEvent(eventId, userId, file);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Photo uploaded successfully",
                            "filePath", filePath
                    ));
        } catch (Exception e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{eventId}/photos")
    public ResponseEntity<?> getEventPhotos(@PathVariable Long eventId) {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            // Get photos via EventService
            List<String> photos = eventService.getEventPhotos(eventId, userId);

            return ResponseEntity.ok(Map.of("photos", photos));
        } catch (Exception e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{eventId}/photos")
    public ResponseEntity<?> deletePhoto(
            @PathVariable Long eventId,
            @RequestParam("filePath") String filePath) {
        try {
            // Get authenticated user from JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find user by email
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            // Delete photo via EventService
            eventService.deletePhotoFromEvent(eventId, userId, filePath);

            return ResponseEntity.ok(Map.of("message", "Photo deleted successfully"));
        } catch (Exception e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", e.getMessage()));
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}