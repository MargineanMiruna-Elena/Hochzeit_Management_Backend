package com.example.backend.dto;

import lombok.Data;

@Data
public class CreateOrganizerRequest {
    private String mail;
    private String userId;
}
