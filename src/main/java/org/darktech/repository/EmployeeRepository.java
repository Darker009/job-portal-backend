package org.darktech.repository;

import org.darktech.entity.CandidateProfile;
import org.darktech.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
}

