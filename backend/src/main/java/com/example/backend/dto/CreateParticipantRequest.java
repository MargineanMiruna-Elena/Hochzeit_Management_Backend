package com.example.backend.dto;

import lombok.Data;

//to be implemented later

@Data
public class CreateParticipantRequest {
    private String name;
    private String email;
    private Long eventId;
}