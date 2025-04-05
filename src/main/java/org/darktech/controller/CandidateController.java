package org.darktech.controller;

import org.darktech.common.CommonFunction;
import org.darktech.entity.CandidateProfile;
import org.darktech.entity.User;
import org.darktech.service.CandidateService;
import org.darktech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonFunction commonFunction;

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('Candidate')")
    public ResponseEntity<?> saveCandidate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("collegeName") String collegeName,
            @RequestParam("degree") String degree,
            @RequestParam("specialization") String specialization,
            @RequestParam("contactNumber") Long contactNumber,
            @RequestParam("skills") String skills,
            @RequestParam("dob") String dob,
            @RequestParam("address") String address,
            @RequestParam("currentLocation") String currentLocation,
            @RequestParam(value = "resumeFile", required = true) MultipartFile resumeFile) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String resumeUrl;
        try {
            resumeUrl = commonFunction.processFileUpload(resumeFile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setUser(user);
        candidateProfile.setCollegeName(collegeName);
        candidateProfile.setDegree(degree);
        candidateProfile.setSpecialization(specialization);
        candidateProfile.setContactNumber(contactNumber);
        candidateProfile.setSkills(skills);

        try {
            candidateProfile.setDob(java.sql.Date.valueOf(dob));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid date format for dob. Use YYYY-MM-DD"));
        }

        candidateProfile.setAddress(address);
        candidateProfile.setCurrentLocation(currentLocation);
        candidateProfile.setResumeUrl(resumeUrl);

        try {
            CandidateProfile savedCandidate = candidateService.saveCandidate(candidateProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to save candidate profile"));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('Candidate')")
    public ResponseEntity<CandidateProfile> getCandidate(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<CandidateProfile> candidateProfile = candidateService.getCandidate(user.getId());
        return candidateProfile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/profile/picture")
    @PreAuthorize("hasAuthority('Candidate')")
    public ResponseEntity<?> uploadProfilePicture(
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
            String pictureUrl = commonFunction.processFileUpload(file);
            Optional<CandidateProfile> optProfile = candidateService.getCandidate(user.getId());

            if (optProfile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Candidate profile not found"));
            }

            CandidateProfile candidateProfile = optProfile.get();
            candidateProfile.setProfilePicture(pictureUrl);
            CandidateProfile updatedProfile = candidateService.saveCandidate(candidateProfile);
            return ResponseEntity.ok(updatedProfile);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload profile picture"));
        }
    }
}