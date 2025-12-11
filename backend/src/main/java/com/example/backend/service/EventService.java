package com.example.backend.service;

import com.example.backend.dto.CreateEventRequest;
import com.example.backend.dto.UpdateEventRequest;
import com.example.backend.dto.EventResponse;
import com.example.backend.model.Event;
import com.example.backend.model.Location;
import com.example.backend.model.User;
import com.example.backend.model.Organizer;
import com.example.backend.repository.EventRepository;
import com.example.backend.repository.LocationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.OrganizerRepository;
import com.example.backend.repository.ParticipantRepository;
// import com.example.backend.repository.PhotoRepository;
// import com.example.backend.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    // @Autowired
    // private PhotoRepository photoRepository;

    // @Autowired
    // private InvitationRepository invitationRepository;

    @Transactional
    public EventResponse createEvent(Long userId, CreateEventRequest request) {
        // Step 1: Verify user exists (this is the creator/first organizer)
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Step 2: Auto-fill emailOrg1 with authenticated user's email
        String emailOrg1 = creator.getEmail();
        String emailOrg2 = request.getEmailOrg2();

        // Step 3: Validate emailOrg2
        // 3a: Check emailOrg2 is not empty
        if (emailOrg2 == null || emailOrg2.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Second organizer email (emailOrg2) is required");
        }

        // 3b: Check emailOrg2 is different from emailOrg1 (no duplicates)
        if (emailOrg1.equalsIgnoreCase(emailOrg2)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Both organizers cannot be the same person. emailOrg1 and emailOrg2 must be different.");
        }

        // 3c: Check emailOrg2 user exists in database
        User secondOrganizer = userRepository.findByEmail(emailOrg2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Second organizer with email '" + emailOrg2 + "' does not have an account. " +
                                "Both organizers must be registered users."));

        // Step 4: Validate all required fields BEFORE saving
        // 4a: Validate name is not blank
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Event name is required and cannot be blank");
        }

        // 4b: Validate description is not blank
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Event description is required and cannot be blank");
        }

        // 4c: Validate locationId is not null
        if (request.getLocationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location ID is required");
        }

        // 4d: Validate dates
        if (request.getStartDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date is required");
        }
        if (request.getEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date is required");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be after end date");
        }

        // 4e: Verify location exists
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));

        // Step 5: ALL VALIDATIONS PASSED - Now create Event (within transaction)
        Event event = new Event();
        event.setName(request.getName());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setDescription(request.getDescription());
        event.setEmailOrg1(emailOrg1);      // Auto-filled from authenticated user
        event.setEmailOrg2(emailOrg2);      // Provided in request
        event.setLocationID(request.getLocationId());
        event.setImageUrl(request.getImageUrl());  // Add this line

        Event savedEvent = eventRepository.save(event);

        // Step 6: Auto-create Organizer records for both organizers
        // (within same transaction - if this fails, entire transaction rolls back)
        createOrganizerIfNotExists(creator, emailOrg1);
        createOrganizerIfNotExists(secondOrganizer, emailOrg2);

        return convertToResponse(savedEvent, location);
    }

    /**
     * Helper method to create Organizer if it doesn't already exist
     */
    private void createOrganizerIfNotExists(User user, String email) {
        if (!organizerRepository.existsByMail(email)) {
            Organizer organizer = new Organizer();
            organizer.setMail(email);
            organizer.setUser(user);
            organizerRepository.save(organizer);
        }
    }

    public List<EventResponse> getEventsByOrganizer(Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String userEmail = user.getEmail();

        // Get events where user is creator OR organizer
        return eventRepository.findByEmailOrg1OrEmailOrg2(userEmail)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }

    public List<EventResponse> getEventsByParticipant(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String userEmail = user.getEmail();

        // Get events where user is a participant/guest
        return participantRepository.findEventsByUserEmail(userEmail)
                .stream()
                .distinct()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return convertToResponse(event);
    }

    public EventResponse getOrganizerEventById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if user is one of the organizers
        String userEmail = user.getEmail();
        boolean isOrganizer = event.getEmailOrg1().equalsIgnoreCase(userEmail) ||
                event.getEmailOrg2().equalsIgnoreCase(userEmail);

        if (!isOrganizer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to access this event. " +
                            "Only the organizers (emailOrg1 and emailOrg2) can access it.");
        }

        return convertToResponse(event);
    }


    public EventResponse updateEvent(Long userId, Long eventId, UpdateEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if user is one of the organizers
        String userEmail = user.getEmail();
        boolean isOrganizer = event.getEmailOrg1().equalsIgnoreCase(userEmail) ||
                event.getEmailOrg2().equalsIgnoreCase(userEmail);

        if (!isOrganizer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to update this event. " +
                            "Only the organizers can make changes.");
        }

        // Validate dates - both must be provided if either is provided
        if ((request.getStartDate() != null && request.getEndDate() == null) ||
                (request.getStartDate() == null && request.getEndDate() != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Both start date and end date must be provided together. You cannot update only one.");
        }

        // If both dates are provided, validate they're in correct order
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Start date cannot be after end date");
            }
        }

        // Verify location if provided
        if (request.getLocationId() != null) {
            locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
        }

        // Update fields (but NOT emailOrg1 or emailOrg2 - they're immutable)
        if (request.getName() != null && !request.getName().isBlank()) {
            event.setName(request.getName());
        }
        if (request.getStartDate() != null) {
            event.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            event.setEndDate(request.getEndDate());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocationId() != null) {
            event.setLocationID(request.getLocationId());
        }

        try {
            Event updatedEvent = eventRepository.save(event);
            return convertToResponse(updatedEvent);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update event: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if user is one of the organizers
        String userEmail = user.getEmail();
        boolean isOrganizer = event.getEmailOrg1().equalsIgnoreCase(userEmail) ||
                event.getEmailOrg2().equalsIgnoreCase(userEmail);

        if (!isOrganizer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to delete this event. " +
                            "Only the organizers can delete it.");
        }

        try {
            // Step 1: Delete all invitations associated with this event
            // invitationRepository.deleteByEventId(eventId);

            // Step 2: Delete all participants associated with this event
            participantRepository.deleteByEventId(eventId);

            // Step 3: Delete all photos associated with this event
            // photoRepository.deleteByEventId(eventId);

            // Step 4: Delete the event itself
            eventRepository.deleteById(eventId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete event: " + e.getMessage(), e);
        }
    }


    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    private EventResponse convertToResponse(Event event) {
        Location location = null;
        if (event.getLocationID() != null) {
            location = locationRepository.findById(event.getLocationID()).orElse(null);
        }
        return convertToResponse(event, location);
    }


    private EventResponse convertToResponse(Event event, Location location) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getStartDate(),
                event.getEndDate(),
                event.getDescription(),
                event.getEmailOrg1(),
                event.getEmailOrg2(),
                event.getLocationID(),
                event.getCreatorId(),
                location != null ? location.getName() : null,
                location != null ? location.getAddress() : null,
                event.getImageUrl()
        );
    }
}