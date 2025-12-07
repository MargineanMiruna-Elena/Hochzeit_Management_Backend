package com.example.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 255, message = "Event name must be between 3 and 255 characters")
    private String name;

    @NotBlank(message = "Event description is required")
    @Size(min = 10, max = 1000, message = "Event description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;

    @NotBlank(message = "Second organizer email (emailOrg2) is required")
    @Email(message = "Second organizer email must be valid")
    private String emailOrg2;

    @NotNull(message = "Location ID is required")
    @Positive(message = "Location ID must be a positive number")
    private Long locationId;
}