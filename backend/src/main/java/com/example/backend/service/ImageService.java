package com.example.backend.service;

import com.example.backend.model.Event;
import com.example.backend.model.Image;
import com.example.backend.model.Participant;
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
    public Image uploadImage(Event event, Participant uploader, MultipartFile file) {
        String filename = storageService.store(file);

        Image image = new Image();
        image.setFileName(filename);
        image.setEvent(event);
        image.setUploader(uploader);
        image.setUploadTime(LocalDateTime.now());

        return imageRepository.save(image);
    }

    public List<Image> getImagesForEvent(Long eventId) {
        return imageRepository.findByEventId(eventId);
    }

    @Transactional
    public void deleteImage(Long imageId, Participant requester) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Security check: only allow deletion if the requester is the uploader
        if (!image.getUploader().getId().equals(requester.getId())) {
            throw new RuntimeException("You are not authorized to delete this image");
        }

        // Delete from storage
        storageService.delete(image.getFileName());

        // Delete from database
        imageRepository.delete(image);
    }

    public java.nio.file.Path loadImagePath(String filename) {
        return storageService.load(filename);
    }
}
