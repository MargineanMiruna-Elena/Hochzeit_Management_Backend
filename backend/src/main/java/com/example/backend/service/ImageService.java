package com.example.backend.service;

import com.example.backend.dto.ImageResponse;
import com.example.backend.model.Event;
import com.example.backend.model.Image;
import com.example.backend.model.Participant;
import com.example.backend.model.User;
import com.example.backend.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageStorageService storageService;

    @Transactional
    public Image uploadImage(Event event, User uploader, MultipartFile file) {
        String filename = storageService.store(file);

        Image image = new Image();
        image.setFileName(filename);
        image.setEvent(event);
        image.setUploader(uploader);
        image.setUploadTime(LocalDateTime.now());

        return imageRepository.save(image);
    }

    public List<ImageResponse> getImagesForEvent(Long eventId) {
        return imageRepository.findByEventId(eventId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ImageResponse convertToResponse(Image image) {
        return new ImageResponse(
                image.getId(),
                "/uploads/" + image.getFileName(),
                image.getEvent().getId(),
                image.getUploader().getId()
        );
    }

    public Event getEventOfImage(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new RuntimeException("Image not found");
        }

        return imageRepository.findById(id).get().getEvent();
    }

    @Transactional
    public void deleteImage(Long imageId, User requester, Boolean isOrganizer) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Security check: only allow deletion if the requester is the uploader
        if (!image.getUploader().getId().equals(requester.getId()) && !isOrganizer) {
            throw new RuntimeException("You are not authorized to delete this image");
        }

        // Delete from storage
        storageService.delete(image.getFileName());

        // Delete from database
        imageRepository.deleteById(imageId);
    }

    public java.nio.file.Path loadImagePath(String filename) {
        return storageService.load(filename);
    }
}
