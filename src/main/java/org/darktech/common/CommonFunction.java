package org.darktech.common;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Component
public class CommonFunction {

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory: " + uploadDir, e);
        }
    }

    public String processFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        validateFile(file);
        try {
            String uniqueFileName = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
            Path filePath = Paths.get(uploadDir, uniqueFileName);
            Files.copy(file.getInputStream(), filePath);
            return "/uploads/resumes/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    public void validateFile(MultipartFile file) {
        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!Set.of(".pdf", ".doc", ".docx", ".png", ".jpg", ".jpeg", ".gif").contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid file type uploaded");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }
    }

    public String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "" : filename.substring(lastDot);
    }
}
