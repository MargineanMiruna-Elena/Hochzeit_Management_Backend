package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invitation_response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;
    
    @Column(name = "food_preferences", length = 500)
    private String foodPreferences;
    
    public InvitationResponse(Event event, Participant participant) {
        this.event = event;
        this.participant = participant;
    }
}