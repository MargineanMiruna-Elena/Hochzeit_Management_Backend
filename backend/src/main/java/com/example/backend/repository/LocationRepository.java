package com.example.backend.repository;

import com.example.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Find location by name
    Optional<Location> findByName(String name);

    // Find locations by name (partial match, case-insensitive)
    List<Location> findByNameContainingIgnoreCase(String name);

    // Find locations by address (partial match, case-insensitive)
    List<Location> findByAddressContainingIgnoreCase(String address);

    // Check if location exists by name
    boolean existsByName(String name);

    // Check if location exists by address
    boolean existsByAddress(String address);

    // Find location by name and address (exact match)
    Optional<Location> findByNameAndAddress(String name, String address);

    Optional<Location> findLocationById(Long locationID);
}