package org.darktech.service;

import org.darktech.entity.CandidateProfile;
import org.darktech.entity.User;
import org.darktech.repository.CandidateRepository;
import org.darktech.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public CandidateService(CandidateRepository candidateRepository, UserRepository userRepository) {
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CandidateProfile saveCandidate(CandidateProfile candidateProfile) {
        if (candidateProfile.getUser() == null || candidateProfile.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        // Validate the user exists
        User user = userRepository.findById(candidateProfile.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // If a candidate profile already exists, update its fields; otherwise, create a new one.
        Optional<CandidateProfile> existingProfileOpt = candidateRepository.findByUser_Id(user.getId());
        CandidateProfile profileToSave;
        if (existingProfileOpt.isPresent()) {
            profileToSave = existingProfileOpt.get();
            updateProfileFields(profileToSave, candidateProfile);
        } else {
            candidateProfile.setUser(user);
            profileToSave = candidateProfile;
        }

        return candidateRepository.save(profileToSave);
    }

    public Optional<CandidateProfile> getCandidate(Long userId) {
        return candidateRepository.findByUser_Id(userId);
    }

    private void updateProfileFields(CandidateProfile existing, CandidateProfile updated) {
        if (updated.getCollegeName() != null) {
            existing.setCollegeName(updated.getCollegeName());
        }
        if (updated.getDegree() != null) {
            existing.setDegree(updated.getDegree());
        }
        if (updated.getSpecialization() != null) {
            existing.setSpecialization(updated.getSpecialization());
        }
        if (updated.getContactNumber() != null) {
            existing.setContactNumber(updated.getContactNumber());
        }
        if (updated.getSkills() != null) {
            existing.setSkills(updated.getSkills());
        }
        if (updated.getDob() != null) {
            existing.setDob(updated.getDob());
        }
        if (updated.getAddress() != null) {
            existing.setAddress(updated.getAddress());
        }
        if (updated.getCurrentLocation() != null) {
            existing.setCurrentLocation(updated.getCurrentLocation());
        }
        if (updated.getResumeUrl() != null) {
            existing.setResumeUrl(updated.getResumeUrl());
        }
    }
}
