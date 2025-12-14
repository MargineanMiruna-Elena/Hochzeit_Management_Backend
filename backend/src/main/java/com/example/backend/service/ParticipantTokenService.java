package com.example.backend.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.Event;
import com.example.backend.model.Participant;
import com.example.backend.model.ParticipantToken;
import com.example.backend.repository.ParticipantTokenRepository;

@Service
public class ParticipantTokenService {
    
    @Autowired
    private ParticipantTokenRepository participantTokenRepository;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public String generateTokenForParticipant(Event event, Participant participant) {
        Optional<ParticipantToken> existingToken = participantTokenRepository
            .findByParticipantIdAndEventId(participant.getId(), event.getId());
        
        if (existingToken.isPresent() && !isTokenExpired(existingToken.get())) {
            return existingToken.get().getToken();
        }
        
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        
        //  expiration (30 days from now)
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        
        ParticipantToken participantToken = new ParticipantToken(token, participant, event, expiresAt);
        participantTokenRepository.save(participantToken);
        
        return token;
    }
    
    public Optional<ParticipantToken> validateToken(String token) {
        Optional<ParticipantToken> participantToken = participantTokenRepository.findByToken(token);
        
        if (participantToken.isEmpty()) {
            return Optional.empty();
        }
        
        ParticipantToken pToken = participantToken.get();
        
        if (isTokenExpired(pToken)) {
            return Optional.empty();
        }
        
        return participantToken;
    }
    
    public void markTokenAsUsed(String token) {
        Optional<ParticipantToken> participantToken = participantTokenRepository.findByToken(token);
        if (participantToken.isPresent()) {
            ParticipantToken pToken = participantToken.get();
            pToken.setUsed(true);
            participantTokenRepository.save(pToken);
        }
    }
    
    private boolean isTokenExpired(ParticipantToken token) {
        return token.getExpiresAt() != null && LocalDateTime.now().isAfter(token.getExpiresAt());
    }
    
    public void cleanupExpiredTokens() {
        participantTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}