package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RsvpRequest {
    private Boolean accept;
    private String foodPreferences;
    private Boolean needsParking;
}
