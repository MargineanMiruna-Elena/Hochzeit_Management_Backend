package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.InvitationResponse;

@Repository
public interface InvitationResponseRepository extends JpaRepository<InvitationResponse, Long> {
    
    Optional<InvitationResponse> findByEventIdAndParticipantId(Long eventId, Long participantId);
    
    List<InvitationResponse> findByEventId(Long eventId);
    
    List<InvitationResponse> findByParticipantId(Long participantId);
    
    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);
}