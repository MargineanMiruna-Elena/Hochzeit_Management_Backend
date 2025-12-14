package com.example.backend.repository;

import com.example.backend.model.Organizer;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, String> {

    // findById(mail) is already provided by JpaRepository
    // no need for findByMail since mail is primary key

    // find organizer for a specific user
    Optional<Organizer> findByUser(User user);

    boolean existsByMail(String mail);

    // find organizer by userId
    Optional<Organizer> findByUserId(Long userId);

    // check if user is organizer
    boolean existsByUserId(Long userId);
}