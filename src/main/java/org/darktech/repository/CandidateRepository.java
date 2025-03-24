package org.darktech.repository;

import org.darktech.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateProfile, Long> {
    Optional<CandidateProfile> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
}
