package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.ParticipantToken;

@Repository
public interface ParticipantTokenRepository extends JpaRepository<ParticipantToken, Long> {
    
    Optional<ParticipantToken> findByToken(String token);
    
    Optional<ParticipantToken> findByParticipantIdAndEventId(Long participantId, Long eventId);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    boolean existsByToken(String token);
}