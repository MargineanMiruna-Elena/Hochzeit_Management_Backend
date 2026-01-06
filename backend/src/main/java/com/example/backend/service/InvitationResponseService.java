package com.example.backend.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.model.Event;
import com.example.backend.model.InvitationResponse;
import com.example.backend.model.Participant;
import com.example.backend.repository.EventRepository;
import com.example.backend.repository.InvitationResponseRepository;
import com.example.backend.repository.ParticipantRepository;

@Service
public class InvitationResponseService {
    
    @Autowired
    private InvitationResponseRepository invitationResponseRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    public InvitationResponseDTO updateInvitationResponse(
            Long eventId, 
            Long participantId, 
            String foodPreferences, 
            Boolean isAttending, Boolean needsParking) {
        
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Participant> participantOpt = participantRepository.findById(participantId);
        
        if (eventOpt.isEmpty() || participantOpt.isEmpty()) {
            throw new RuntimeException("Event or participant not found");
        }
        
        Event event = eventOpt.get();
        Participant participant = participantOpt.get();
        
        // Update participant's attending status
        participant.setAttending(isAttending);
        participantRepository.save(participant);
        
        // Get or create invitation response
        InvitationResponse response = invitationResponseRepository
            .findByEventIdAndParticipantId(eventId, participantId)
            .orElse(new InvitationResponse(event, participant, foodPreferences, needsParking));
        
        // Set food preferences only if attending
        if (Boolean.TRUE.equals(isAttending)) {
            response.setFoodPreferences(foodPreferences);
            response.setNeedsParking(needsParking);
        } else {
            response.setFoodPreferences(null);
            response.setNeedsParking(null);
        }
        
        InvitationResponse savedResponse = invitationResponseRepository.save(response);
        
        return new InvitationResponseDTO(savedResponse, participantId, isAttending);
    }
    
    public static class InvitationResponseDTO {
        private Long participantId;
        private Boolean isAttending;
        private InvitationResponse response;
        
        public InvitationResponseDTO(InvitationResponse response, Long participantId, Boolean isAttending) {
            this.response = response;
            this.participantId = participantId;
            this.isAttending = isAttending;
        }
        
        public Long getParticipantId() { return participantId; }
        public void setParticipantId(Long participantId) { this.participantId = participantId; }
        
        public Boolean getIsAttending() { return isAttending; }
        public void setIsAttending(Boolean isAttending) { this.isAttending = isAttending; }
        
        public InvitationResponse getResponse() { return response; }
        public void setResponse(InvitationResponse response) { this.response = response; }
    }
}