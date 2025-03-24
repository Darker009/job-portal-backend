package org.darktech.controller;

import jakarta.annotation.PostConstruct;
import org.darktech.entity.CandidateProfile;
import org.darktech.entity.User;
import org.darktech.service.CandidateService;
import org.darktech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserService userService;

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

    // Existing /profile endpoints here...
    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('Candidate')")
    public ResponseEntity<CandidateProfile> saveCandidate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("collegeName") String collegeName,
            @RequestParam("degree") String degree,
            @RequestParam("specialization") String specialization,
            @RequestParam("contactNumber") Long contactNumber,
            @RequestParam("skills") String skills,
            @RequestParam("dob") String dob,
            @RequestParam("address") String address,
            @RequestParam("currentLocation") String currentLocation,
            @RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String resumeUrl = processFileUpload(resumeFile);
        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setUser(user);
        candidateProfile.setCollegeName(collegeName);
        candidateProfile.setDegree(degree);
        candidateProfile.setSpecialization(specialization);
        candidateProfile.setContactNumber(contactNumber);
        candidateProfile.setSkills(skills);
        candidateProfile.setDob(java.sql.Date.valueOf(dob));
        candidateProfile.setAddress(address);
        candidateProfile.setCurrentLocation(currentLocation);
        candidateProfile.setResumeUrl(resumeUrl);

        try {
            CandidateProfile savedCandidate = candidateService.saveCandidate(candidateProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('Candidate')")
    public ResponseEntity<CandidateProfile> getCandidate(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<CandidateProfile> candidateProfile = candidateService.getCandidate(user.getId());
        return candidateProfile
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // New endpoint for profile picture upload
    @PostMapping("/profile-picture")
    @PreAuthorize("hasAuthority('Candidate')")
    public ResponseEntity<CandidateProfile> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("profilePicture") MultipartFile file) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            String pictureUrl = processFileUpload(file);
            // Update candidate profile with new profile picture URL
            Optional<CandidateProfile> optProfile = candidateService.getCandidate(user.getId());
            if (optProfile.isPresent()) {
                CandidateProfile candidateProfile = optProfile.get();
                candidateProfile.setProfilePicture(pictureUrl);
                CandidateProfile updatedProfile = candidateService.saveCandidate(candidateProfile);
                return ResponseEntity.ok(updatedProfile);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // File upload helper method (used for both resume and profile picture)
    private String processFileUpload(MultipartFile file) {
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

    private void validateFile(MultipartFile file) {
        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        // Allow common image types for profile picture and document types for resume
        if (!Set.of(".pdf", ".doc", ".docx", ".png", ".jpg", ".jpeg", ".gif").contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid file type uploaded");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "" : filename.substring(lastDot);
    }
}
