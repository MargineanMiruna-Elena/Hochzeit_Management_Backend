package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private String emailOrg1;

    private String emailOrg2;

    private Long locationId;

    private Long creatorId;

    private String locationName;

    private String locationAddress;

    private String imageUrl;

    public EventResponse(Long id, String name, LocalDate startDate, LocalDate endDate,
                         String description, String emailOrg1, String emailOrg2,
                         Long locationId, String locationName, String locationAddress, String imageUrl, Long creatorId) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.emailOrg1 = emailOrg1;
        this.emailOrg2 = emailOrg2;
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.imageUrl = imageUrl;
        this.creatorId = creatorId;
    }
}