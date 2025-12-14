package com.example.backend.dto;

import java.util.Set;

import com.example.backend.enums.FoodPreference;
import com.example.backend.enums.TransportationMethod;
import com.example.backend.model.Event;
import com.example.backend.model.InvitationResponse;
import com.example.backend.model.Participant;

public class InvitationDetailsDTO {
    private Event event;
    private Participant participant;
    private InvitationResponse response;
    private InvitationQuestionsDTO availableQuestions;

    public InvitationDetailsDTO(Event event, Participant participant, InvitationResponse response) {
        this.event = event;
        this.participant = participant;
        this.response = response;
        this.availableQuestions = new InvitationQuestionsDTO(event);
    }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    
    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }
    
    public InvitationResponse getResponse() { return response; }
    public void setResponse(InvitationResponse response) { this.response = response; }
    
    public InvitationQuestionsDTO getAvailableQuestions() { return availableQuestions; }
    public void setAvailableQuestions(InvitationQuestionsDTO availableQuestions) { this.availableQuestions = availableQuestions; }

    public static class InvitationQuestionsDTO {
        private Boolean askFoodPreferences;
        private Boolean askTransportation;
        private Set<FoodPreference> availableFoodPreferences;
        private Set<TransportationMethod> availableTransportationMethods;

        public InvitationQuestionsDTO(Event event) {
            this.askFoodPreferences = event.getAskFoodPreferences();
            this.askTransportation = event.getAskTransportation();
            this.availableFoodPreferences = event.getAvailableFoodPreferences();
            this.availableTransportationMethods = event.getAvailableTransportationMethods();
        }

        public Boolean getAskFoodPreferences() { return askFoodPreferences; }
        public void setAskFoodPreferences(Boolean askFoodPreferences) { this.askFoodPreferences = askFoodPreferences; }
        
        public Boolean getAskTransportation() { return askTransportation; }
        public void setAskTransportation(Boolean askTransportation) { this.askTransportation = askTransportation; }
        
        public Set<FoodPreference> getAvailableFoodPreferences() { return availableFoodPreferences; }
        public void setAvailableFoodPreferences(Set<FoodPreference> availableFoodPreferences) { this.availableFoodPreferences = availableFoodPreferences; }
        
        public Set<TransportationMethod> getAvailableTransportationMethods() { return availableTransportationMethods; }
        public void setAvailableTransportationMethods(Set<TransportationMethod> availableTransportationMethods) { this.availableTransportationMethods = availableTransportationMethods; }
    }
}