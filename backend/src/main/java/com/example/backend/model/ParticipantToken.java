package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "participant_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token", unique = true, nullable = false)
    private String token;
    
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "used", nullable = false)
    private Boolean used = false;

    

    public ParticipantToken(String token, Participant participant, Event event, LocalDateTime expiresAt) {
        this.token = token;
        this.participant = participant;
        this.event = event;
        this.expiresAt = expiresAt;
    }

}    