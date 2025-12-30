package com.example.backend.model;

import com.example.backend.config.JsonListConverter;
import com.example.backend.enums.FoodPreference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name ="start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "email_org1", nullable = false, updatable = false)
    private String emailOrg1;

    @Column(name = "email_org2", updatable = false)
    private String emailOrg2;

    @Column(name = "location_id", nullable = false)
    private Long locationID;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "event_images", columnDefinition = "JSON")
    @Convert(converter = JsonListConverter.class)
    private List<String> eventImages;

    @Column(name = "has_parking", nullable = false)
    private Boolean hasParking = false;

    @ElementCollection(targetClass = FoodPreference.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "event_food_preferences", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "food_preference")
    @Enumerated(EnumType.STRING)
    private Set<FoodPreference> foodPreferences = new HashSet<>();
}