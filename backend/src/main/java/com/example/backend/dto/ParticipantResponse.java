// com.example.backend.dto.ParticipantResponse
package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {
    private Long id;
    private String name;
    private String email;
    private Boolean attending;
    private Long eventId;
}
