package com.example.backend.repository;

import com.example.backend.model.Participant;
import com.example.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // Find all participants for a specific event
    List<Participant> findByEventId(Long eventId);

    // Find participant by email and event
    Optional<Participant> findByEmailAndEventId(String email, Long eventId);

    // Find all participants with a specific email across all events
    List<Participant> findByEmail(String email);

    // Find all events where a user is a participant (by user email)
    @Query("SELECT DISTINCT p.event FROM Participant p WHERE p.email = :email")
    List<Event> findEventsByUserEmail(@Param("email") String email);

    // Delete all participants for an event
    long deleteByEventId(Long eventId);

    // Check if participant exists for event
    boolean existsByEmailAndEventId(String email, Long eventId);

    // Count participants for event
    long countByEventId(Long eventId);

    // Find all accepted participants for event
    List<Participant> findByEventIdAndAttendingTrue(Long eventId);

    // Find all declined participants for event
    List<Participant> findByEventIdAndAttendingFalse(Long eventId);

    // Find all pending participants (no response yet)
    List<Participant> findByEventIdAndAttendingIsNull(Long eventId);
}