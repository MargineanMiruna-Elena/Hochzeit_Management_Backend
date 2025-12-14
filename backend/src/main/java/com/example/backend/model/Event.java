package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.example.backend.enums.FoodPreference;

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

    @Temporal(TemporalType.DATE)
    @Column(name ="start_date", nullable = false)
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "email_org1", nullable = false, updatable = false)
    private String emailOrg1;

    @Column(name = "email_org2", nullable = false, updatable = false)
    private String emailOrg2;

    @Column(name = "location_id", nullable = false)
    private Long locationID;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;


    
    @Column(name = "ask_food_preferences", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean askFoodPreferences = false;
    
    
    public Event(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    
}  
