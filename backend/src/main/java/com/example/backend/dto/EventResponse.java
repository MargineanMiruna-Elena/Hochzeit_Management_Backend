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
}