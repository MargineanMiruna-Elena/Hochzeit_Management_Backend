package com.example.backend.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.backend.enums.FoodPreference;
import com.example.backend.enums.TransportationMethod;

import jakarta.persistence.*;

@Entity
@Table(name = "invitation_response")
public class InvitationResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;
    
    @ElementCollection(targetClass = FoodPreference.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "invitation_food_preferences", 
                    joinColumns = @JoinColumn(name = "invitation_response_id"))
    @Column(name = "food_preference")
    private Set<FoodPreference> foodPreferences;
    
    @Enumerated(EnumType.STRING)
    private TransportationMethod transportationMethod;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime respondedAt;

    public InvitationResponse() {}

    public InvitationResponse(Event event, Participant participant) {
        this.event = event;
        this.participant = participant;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    
    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }
    
    public Set<FoodPreference> getFoodPreferences() { return foodPreferences; }
    public void setFoodPreferences(Set<FoodPreference> foodPreferences) { this.foodPreferences = foodPreferences; }
    
    public TransportationMethod getTransportationMethod() { return transportationMethod; }
    public void setTransportationMethod(TransportationMethod transportationMethod) { this.transportationMethod = transportationMethod; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}