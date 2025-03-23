package org.darktech.service;

import org.darktech.entity.CandidateProfile;
import org.darktech.entity.User;
import org.darktech.repository.CandidateRepository;
import org.darktech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    public CandidateProfile saveCandidate(CandidateProfile candidateProfile) {
        if (candidateProfile.getUser() != null && candidateProfile.getUser().getId() != null) {
            User savedUser = userRepository.findById(candidateProfile.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            candidateProfile.setUser(savedUser);
        }
        return candidateRepository.save(candidateProfile);
    }


    public Optional<CandidateProfile> getCandidate(Long id) {
        return candidateRepository.findByUser_Id(id);
    }
}
