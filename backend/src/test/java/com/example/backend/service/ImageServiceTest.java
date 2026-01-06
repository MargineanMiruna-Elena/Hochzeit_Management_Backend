package com.example.backend.service;

import com.example.backend.model.Event;
import com.example.backend.model.Image;
import com.example.backend.model.Participant;
import com.example.backend.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageStorageService storageService;

    @InjectMocks
    private ImageService imageService;

    private Participant participant;
    private Event event;
    private Image image;

    @BeforeEach
    void setUp() {
        participant = new Participant();
        participant.setId(1L);
        participant.setName("Test User");

        event = new Event();
        event.setId(1L);
        event.setName("Test Event");

        image = new Image();
        image.setId(1L);
        image.setFileName("test-image.jpg");
        image.setEvent(event);
        image.setUploader(participant);
    }

    @Test
    void uploadImage_ShouldSaveImage_WhenValid() {
        MultipartFile file = mock(MultipartFile.class);
        when(storageService.store(file)).thenReturn("generated-filename.jpg");
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Image result = imageService.uploadImage(event, participant, file);

        assertNotNull(result);
        assertEquals("generated-filename.jpg", result.getFileName());
        assertEquals(participant, result.getUploader());
        assertEquals(event, result.getEvent());
        assertNotNull(result.getUploadTime());

        verify(storageService).store(file);
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void deleteImage_ShouldDelete_WhenUserIsOwner() {
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        imageService.deleteImage(1L, participant);

        verify(storageService).delete("test-image.jpg");
        verify(imageRepository).delete(image);
    }

    @Test
    void deleteImage_ShouldThrowException_WhenUserIsNotOwner() {
        Participant otherUser = new Participant();
        otherUser.setId(2L);
        otherUser.setName("Other User");

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageService.deleteImage(1L, otherUser);
        });

        assertEquals("You are not authorized to delete this image", exception.getMessage());

        verify(storageService, never()).delete(anyString());
        verify(imageRepository, never()).delete(any(Image.class));
    }

    @Test
    void deleteImage_ShouldThrowException_WhenImageNotFound() {
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageService.deleteImage(999L, participant);
        });

        assertEquals("Image not found", exception.getMessage());
    }
}
