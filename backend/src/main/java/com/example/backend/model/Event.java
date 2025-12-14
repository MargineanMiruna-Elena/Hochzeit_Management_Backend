package com.example.backend.model;

import com.example.backend.config.JsonListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

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

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "event_images", columnDefinition = "JSON")
    @Convert(converter = JsonListConverter.class)
    private List<String> eventImages;

    public Event(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventImages = new ArrayList<>();
    }
}