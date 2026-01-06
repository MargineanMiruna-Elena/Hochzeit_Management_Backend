package com.example.backend.service.impl;

import com.example.backend.dto.CreateParticipantRequest;
import com.example.backend.dto.ParticipantResponse;
import com.example.backend.dto.UpdateParticipantRequest;
import com.example.backend.model.Event;
import com.example.backend.model.InvitationResponse;
import com.example.backend.model.Participant;
import com.example.backend.repository.EventRepository;
import com.example.backend.repository.InvitationResponseRepository;
import com.example.backend.repository.ParticipantRepository;
import com.example.backend.service.ParticipantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final InvitationResponseRepository invitationResponseRepository;

    public ParticipantServiceImpl(ParticipantRepository participantRepository,
                                  EventRepository eventRepository,
                                  InvitationResponseRepository invitationResponseRepository) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.invitationResponseRepository = invitationResponseRepository;
    }

    @Override
    public ParticipantResponse createParticipant(CreateParticipantRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + request.getEventId()));

        Participant participant = new Participant();
        participant.setName(request.getName());
        participant.setEmail(request.getEmail());
        participant.setAttending(request.getAttending());
        participant.setEvent(event);

        Participant saved = participantRepository.save(participant);
        return toResponse(saved);
    }

    @Override
    public ParticipantResponse updateParticipant(Long participantId, UpdateParticipantRequest request) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));

        if (request.getName() != null) {
            participant.setName(request.getName());
        }
        if (request.getEmail() != null) {
            participant.setEmail(request.getEmail());
        }
        if (request.getAttending() != null) {
            participant.setAttending(request.getAttending());
        }

        Participant saved = participantRepository.save(participant);
        return toResponse(saved);
    }

    @Override
    public void deleteParticipant(Long participantId) {
        if (!participantRepository.existsById(participantId)) {
            throw new IllegalArgumentException("Participant not found: " + participantId);
        }
        participantRepository.deleteById(participantId);
    }

    @Override
    public ParticipantResponse getParticipant(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found: " + participantId));
        return toResponse(participant);
    }

    @Override
    public List<ParticipantResponse> getParticipantsByEvent(Long eventId) {
        List<Participant> participants = participantRepository.findByEventId(eventId);
        return participants.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ParticipantResponse> getAcceptedParticipantsByEvent(Long eventId) {
        return participantRepository.findByEventIdAndAttendingTrue(eventId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ParticipantResponse> getDeclinedParticipantsByEvent(Long eventId) {
        return participantRepository.findByEventIdAndAttendingFalse(eventId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ParticipantResponse> getPendingParticipantsByEvent(Long eventId) {
        return participantRepository.findByEventIdAndAttendingIsNull(eventId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ParticipantResponse toResponse(Participant participant) {
        String foodPreference = null;
        Boolean needsParking = null;

        if (participant.getEvent() != null) {
            Optional<InvitationResponse> invitationResponse =
                    invitationResponseRepository.findByEventIdAndParticipantId(
                            participant.getEvent().getId(),
                            participant.getId()
                    );

            if (invitationResponse.isPresent()) {
                foodPreference = invitationResponse.get().getFoodPreferences();
                needsParking = invitationResponse.get().getNeedsParking();
            }
        }

        return new ParticipantResponse(
                participant.getId(),
                participant.getName(),
                participant.getEmail(),
                participant.getAttending(),
                participant.getEvent() != null ? participant.getEvent().getId() : null,
                foodPreference,
                needsParking
        );
    }
}