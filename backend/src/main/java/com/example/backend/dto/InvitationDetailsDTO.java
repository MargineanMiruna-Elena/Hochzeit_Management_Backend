package com.example.backend.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.backend.enums.FoodPreference;
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
        private List<Map<String, String>> availableFoodPreferences;

        public InvitationQuestionsDTO(Event event) {
            this.askFoodPreferences = event.getAskFoodPreferences();
            
            if (Boolean.TRUE.equals(this.askFoodPreferences)) {
                this.availableFoodPreferences = Arrays.stream(FoodPreference.values())
                    .map(fp -> Map.of(
                        "value", fp.name(),
                        "label", fp.getDisplayName()
                    ))
                    .collect(Collectors.toList());
            }
        }    

        public Boolean getAskFoodPreferences() { return askFoodPreferences; }
        public void setAskFoodPreferences(Boolean askFoodPreferences) { this.askFoodPreferences = askFoodPreferences; }
        
        public List<Map<String, String>> getAvailableFoodPreferences() { return availableFoodPreferences; }
        public void setAvailableFoodPreferences(List<Map<String, String>> availableFoodPreferences) { this.availableFoodPreferences = availableFoodPreferences; }
        
    }
}