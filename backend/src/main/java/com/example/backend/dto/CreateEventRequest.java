package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String emailOrg2;
    private Long locationId;
}