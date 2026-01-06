package com.example.backend.controller;

import com.example.backend.config.ParticipantAuthenticationToken;
import com.example.backend.model.Event;
import com.example.backend.model.Image;
import com.example.backend.model.Participant;
import com.example.backend.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/invitation/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (!(authentication instanceof ParticipantAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        ParticipantAuthenticationToken token = (ParticipantAuthenticationToken) authentication;
        Participant participant = token.getParticipant();
        Event event = token.getEvent();

        try {
            Image image = imageService.uploadImage(event, participant, file);
            return ResponseEntity.ok(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Image>> getImages(Authentication authentication) {
        if (!(authentication instanceof ParticipantAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ParticipantAuthenticationToken token = (ParticipantAuthenticationToken) authentication;
        Event event = token.getEvent();

        List<Image> images = imageService.getImagesForEvent(event.getId());
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName, Authentication authentication) {
        // Optional: Check token if you want images to be private to the event
        if (!(authentication instanceof ParticipantAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            java.nio.file.Path file = imageService.loadImagePath(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Or determine type dynamically
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id, Authentication authentication) {
        if (!(authentication instanceof ParticipantAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        ParticipantAuthenticationToken token = (ParticipantAuthenticationToken) authentication;
        Participant participant = token.getParticipant();

        try {
            imageService.deleteImage(id, participant);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete image: " + e.getMessage());
        }
    }
}
