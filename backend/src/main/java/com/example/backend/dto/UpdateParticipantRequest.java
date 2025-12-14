// com.example.backend.dto.UpdateParticipantRequest
package com.example.backend.dto;

import lombok.Data;

@Data
public class UpdateParticipantRequest {
    private String name;
    private String email;
    private Boolean attending;
}
