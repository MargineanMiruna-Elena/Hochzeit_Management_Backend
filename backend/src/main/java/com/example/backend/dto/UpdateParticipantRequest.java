package com.example.backend.dto;

import lombok.Data;

//to be implemented

@Data
public class UpdateParticipantRequest {
    private String email;
    private String newName;
    private Long eventId;
}