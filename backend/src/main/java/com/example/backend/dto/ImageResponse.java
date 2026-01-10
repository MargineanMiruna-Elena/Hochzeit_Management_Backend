package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageResponse {
    Long id;
    String fileName;
    Long eventId;
    Long userId;
}
