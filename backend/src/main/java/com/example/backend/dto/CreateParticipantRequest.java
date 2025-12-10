// com.example.backend.dto.CreateParticipantRequest
package com.example.backend.dto;

import lombok.Data;

@Data
public class CreateParticipantRequest {
    private String name;
    private String email;
    private Boolean attending;
    private Long eventId;
}
