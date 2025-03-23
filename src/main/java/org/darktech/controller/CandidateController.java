package org.darktech.controller;

import org.darktech.entity.CandidateProfile;
import org.darktech.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/candidate")
public class CandidateController {


    @Autowired
    private CandidateService candidateProfileService;

    @PostMapping("/profile")
    public ResponseEntity<?> saveCandidate(@RequestBody CandidateProfile candidateProfile){
        CandidateProfile savedCandidate = candidateProfileService.saveCandidate(candidateProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getCandidate(@PathVariable Long userId){
        Optional<CandidateProfile> profileOptional = candidateProfileService.getCandidate(userId);
        return ResponseEntity.status(HttpStatus.OK).body(profileOptional);
    }

}
