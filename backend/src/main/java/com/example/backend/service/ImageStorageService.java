package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path rootLocation;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public ImageStorageService(@Value("${upload.path:uploads}") String uploadPath) {
        this.rootLocation = Paths.get(uploadPath);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }

            // Validate file size
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new RuntimeException("File size exceeds maximum allowed size");
            }

            // Validate and extract extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("Invalid filename");
            }

            String extension = extractExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new RuntimeException("File type not allowed. Allowed types: jpg, jpeg, png, gif, webp");
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + extension;
            Path destinationFile = this.rootLocation.resolve(filename)
                    .normalize().toAbsolutePath();

            // Security check: ensure file is stored in the correct directory
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory");
            }

            // Store file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public void delete(String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                throw new RuntimeException("Filename is empty");
            }

            Path path = Paths.get(filename).getFileName();
            String cleanName = path.toString();

            Path absoluteRoot = rootLocation.toAbsolutePath().normalize();
            Path file = absoluteRoot.resolve(cleanName).normalize();

            if (!file.getParent().equals(absoluteRoot)) {
                throw new RuntimeException("Security breach: Cannot delete file outside storage directory");
            }

            if (Files.exists(file)) {
                Files.delete(file);
                System.out.println("Fișier șters cu succes!");
            } else {
                System.err.println("Eroare: Fișierul nu există fizic pe disc la calea specificată.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }

    public Path load(String filename) {
        // Security check: validate filename
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new RuntimeException("Invalid filename");
        }

        Path file = rootLocation.resolve(filename).normalize();

        // Ensure file is within root location
        if (!file.getParent().equals(rootLocation.toAbsolutePath())) {
            throw new RuntimeException("Cannot access file outside storage directory");
        }

        return file;
    }

    private String extractExtension(String filename) {
        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}