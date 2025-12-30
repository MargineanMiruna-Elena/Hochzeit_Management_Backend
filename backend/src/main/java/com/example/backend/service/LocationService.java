package com.example.backend.service;

import com.example.backend.dto.CreateLocationRequest;
import com.example.backend.dto.UpdateLocationRequest;
import com.example.backend.dto.LocationResponse;
import com.example.backend.model.Location;
import com.example.backend.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Create a new location
     */
    public LocationResponse createLocation(CreateLocationRequest request) {
        // Validate name is not blank
        if (request.getLocationName() == null || request.getLocationName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location name is required and cannot be blank");
        }

        // Validate address is not blank
        if (request.getLocationAddress() == null || request.getLocationAddress().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location address is required and cannot be blank");
        }

        // Validate coordinates are not blank
        if (request.getLocationCoordinates() == null || request.getLocationCoordinates().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location coordinates are required and cannot be blank");
        }

        // Check if location with same name and address already exists
        if (locationRepository.findByNameAndAddress(request.getLocationName(), request.getLocationAddress()).isPresent()) {
            return convertToResponse(locationRepository.findByNameAndAddress(request.getLocationName(), request.getLocationAddress()).get());
        }

        Location location = new Location();
        location.setName(request.getLocationName().trim());
        location.setAddress(request.getLocationAddress().trim());
        location.setCoordinates(request.getLocationCoordinates().trim());

        try {
            Location savedLocation = locationRepository.save(location);
            return convertToResponse(savedLocation);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create location: " + e.getMessage(), e);
        }
    }

    /**
     * Get location by ID
     */
    public LocationResponse getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Location not found"));
        return convertToResponse(location);
    }

    /**
     * Get all locations
     */
    public List<LocationResponse> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search locations by name (partial match, case-insensitive)
     */
    public List<LocationResponse> searchByName(String query) {
        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Search query cannot be blank");
        }

        List<Location> locations = locationRepository.findByNameContainingIgnoreCase(query.trim());
        return locations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search locations by address (partial match, case-insensitive)
     */
    public List<LocationResponse> searchByAddress(String query) {
        if (query == null || query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Search query cannot be blank");
        }

        List<Location> locations = locationRepository.findByAddressContainingIgnoreCase(query.trim());
        return locations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update location
     */
    public LocationResponse updateLocation(Long id, UpdateLocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Location not found"));

        // Update name if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            location.setName(request.getName().trim());
        }

        // Update address if provided
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            location.setAddress(request.getAddress().trim());
        }

        if (request.getCoordinates() != null && !request.getCoordinates().isBlank()) {
            location.setCoordinates(request.getCoordinates().trim());
        }

        try {
            Location updatedLocation = locationRepository.save(location);
            return convertToResponse(updatedLocation);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update location: " + e.getMessage(), e);
        }
    }

    /**
     * Delete location
     */
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Location not found");
        }

        try {
            locationRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete location: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to convert Location to LocationResponse
     */
    private LocationResponse convertToResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCoordinates()
        );
    }
}