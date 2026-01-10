package com.example.backend.repository;

import com.example.backend.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByEventId(Long eventId);

    List<Image> findByUploaderId(Long uploaderId);
}
