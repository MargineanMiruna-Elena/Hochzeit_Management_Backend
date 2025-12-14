package com.example.backend.service;

import java.time.LocalDateTime;
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
    
    public InvitationResponseDTO updateInvitationResponse(Long eventId, Long participantId, InvitationResponse responseData, Boolean isAttending) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Participant> participantOpt = participantRepository.findById(participantId);
        
        if (eventOpt.isEmpty() || participantOpt.isEmpty()) {
            throw new RuntimeException("Event or participant not found");
        }
        
        Event event = eventOpt.get();
        Participant participant = participantOpt.get();
        
        participant.setAttending(isAttending);
        participantRepository.save(participant);
        
        InvitationResponse response = invitationResponseRepository
            .findByEventIdAndParticipantId(eventId, participantId)
            .orElse(new InvitationResponse(event, participant));
        
//        if (response.getRespondedAt() == null) {
//            response.setRespondedAt(LocalDateTime.now());
//        }
        
        if (Boolean.TRUE.equals(isAttending)) {
            if (Boolean.TRUE.equals(event.getAskFoodPreferences())) {
                if (responseData.getFoodPreferences() != null) {
                    boolean allValid = responseData.getFoodPreferences().stream()
                        .allMatch(pref -> event.getAvailableFoodPreferences().contains(pref));
                    if (!allValid) {
                        throw new IllegalArgumentException("Invalid food preferences selected");
                    }
                }
                response.setFoodPreferences(responseData.getFoodPreferences());
            }
            
            if (Boolean.TRUE.equals(event.getAskTransportation())) {
                if (responseData.getTransportationMethod() != null && 
                    !event.getAvailableTransportationMethods().contains(responseData.getTransportationMethod())) {
                    throw new IllegalArgumentException("Invalid transportation method selected");
                }
                response.setTransportationMethod(responseData.getTransportationMethod());
            }
        } else {
            response.setFoodPreferences(null);
            response.setTransportationMethod(null);
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