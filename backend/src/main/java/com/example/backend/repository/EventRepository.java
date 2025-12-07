package com.example.backend.repository;

import com.example.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find all events created by a user (emailOrg1)
    @Query("SELECT e FROM Event e WHERE e.emailOrg1 = :email")
    List<Event> findByEmailOrg1(@Param("email") String email);

    // Find all events where user is second organizer (emailOrg2)
    @Query("SELECT e FROM Event e WHERE e.emailOrg2 = :email")
    List<Event> findByEmailOrg2(@Param("email") String email);

    // Find events where user is either organizer
    @Query("SELECT e FROM Event e WHERE e.emailOrg1 = :email OR e.emailOrg2 = :email")
    List<Event> findByEmailOrg1OrEmailOrg2(@Param("email") String email);

    // Find events by location
    List<Event> findByLocationID(Long locationId);

    // Check if event exists
    boolean existsById(Long id);

    // Find by name
    List<Event> findByNameContainingIgnoreCase(String name);
}