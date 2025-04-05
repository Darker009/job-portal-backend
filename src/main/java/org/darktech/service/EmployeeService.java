package org.darktech.service;

import jakarta.transaction.Transactional;
import org.darktech.entity.EmployeeProfile;
import org.darktech.entity.User;
import org.darktech.exception.ResourceNotFoundException;
import org.darktech.exception.UserNotFoundException;
import org.darktech.repository.EmployeeRepository;
import org.darktech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {


    private final EmployeeRepository employeeRepository;

    private final UserRepository userRepository;

    public EmployeeService(EmployeeRepository employeeRepository, UserRepository userRepository){
        this.employeeRepository=employeeRepository;
        this.userRepository=userRepository;
    }

    @Transactional
    public EmployeeProfile saveEmployee(EmployeeProfile employeeProfile) {
        if (employeeProfile.getUser() == null || employeeProfile.getUser().getId() == null) {
            throw new ResourceNotFoundException("User ID id required");
        }

        User user = userRepository.findById(employeeProfile.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        Optional<EmployeeProfile> existingProfileOptional = employeeRepository.findByUser_Id(user.getId());
        EmployeeProfile profileToSave;
        if(existingProfileOptional.isPresent()){
            profileToSave = existingProfileOptional.get();
            updateProfileFields(profileToSave, employeeProfile);
        }
        else {
            employeeProfile.setUser(user);
            profileToSave = employeeProfile;
        }

        return employeeRepository.save(profileToSave);
    }

    public Optional<EmployeeProfile> getEmployee(Long id){
        if (id==null || id==0){
            throw new ResourceNotFoundException("User Id required");
        }

        return employeeRepository.findByUser_Id(id);
    }

    private void updateProfileFields(EmployeeProfile existing, EmployeeProfile updated){
        if (updated.getCompanyName() != null){
            existing.setCompanyName(updated.getCompanyName());
        }
        if (updated.getDesignation() != null)
        {
            existing.setDesignation(updated.getDesignation());
        }
        if(updated.getContactNumber() != null){
            existing.setContactNumber(updated.getContactNumber());
        }
        if (updated.getAddress()!=null){
            existing.setAddress(updated.getAddress());
        }
        if (updated.getCurrentLocation()!=null){
            existing.setCurrentLocation(updated.getCurrentLocation());
        }
        if(updated.getDob()!=null){
            existing.setDob(updated.getDob());
        }
        if (updated.getExpUrl()!=null){
            existing.setExpUrl(updated.getExpUrl());
        }
        if (updated.getWorkExperience()!=null)
        {
            existing.setWorkExperience(updated.getWorkExperience());
        }
    }
}
