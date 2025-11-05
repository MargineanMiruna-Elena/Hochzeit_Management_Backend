package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizerResponse {
    private String mail;
    private Long userId;
    private String userName; // for convenience only if frontend wants to show users's name
}