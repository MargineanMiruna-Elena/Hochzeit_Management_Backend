package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.example.backend.enums.FoodPreference;
import com.example.backend.enums.TransportationMethod;

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

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private User creator;

    @Column(name = "ask_food_preferences")
    private Boolean askFoodPreferences = false;
    
    @Column(name = "ask_transportation")
    private Boolean askTransportation = false;
    
    @ElementCollection(targetClass = FoodPreference.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "event_available_food_preferences", 
                    joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "food_preference")
    private Set<FoodPreference> availableFoodPreferences = new HashSet<>();
    
    @ElementCollection(targetClass = TransportationMethod.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "event_available_transportation_methods", 
                    joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "transportation_method")
    private Set<TransportationMethod> availableTransportationMethods = new HashSet<>();


    public Event(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Boolean getAskFoodPreferences() { return askFoodPreferences; }
    public void setAskFoodPreferences(Boolean askFoodPreferences) { this.askFoodPreferences = askFoodPreferences; }
    
    public Boolean getAskTransportation() { return askTransportation; }
    public void setAskTransportation(Boolean askTransportation) { this.askTransportation = askTransportation; }
    
    public Set<FoodPreference> getAvailableFoodPreferences() { return availableFoodPreferences; }
    public void setAvailableFoodPreferences(Set<FoodPreference> availableFoodPreferences) { this.availableFoodPreferences = availableFoodPreferences; }
    
    public Set<TransportationMethod> getAvailableTransportationMethods() { return availableTransportationMethods; }
    public void setAvailableTransportationMethods(Set<TransportationMethod> availableTransportationMethods) { this.availableTransportationMethods = availableTransportationMethods; }
}
