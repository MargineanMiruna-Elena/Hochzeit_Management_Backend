package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO for updating an existing event.
 *
 * All fields are OPTIONAL - organizers can update any combination of fields.
 * However, if startDate is provided, endDate must also be provided, and vice versa.
 *
 * IMMUTABLE FIELDS (cannot be updated):
 * - emailOrg1 (cannot change creator)
 * - emailOrg2 (cannot change 2nd organizer)
 * - creatorId (cannot change who created event)
 *
 * @author Backend Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @Size(min = 3, max = 255, message = "Event name must be between 3 and 255 characters")
    private String name;

    @Size(min = 10, max = 1000, message = "Event description must be between 10 and 1000 characters")
    private String description;

    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;

    @Positive(message = "Location ID must be a positive number")
    private Long locationId;

}