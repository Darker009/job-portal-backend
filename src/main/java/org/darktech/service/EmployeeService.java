package org.darktech.service;

import org.darktech.entity.EmployeeProfile;
import org.darktech.entity.User;
import org.darktech.exception.ResourceNotFoundException;
import org.darktech.exception.UserNotFoundException;
import org.darktech.repository.EmployeeRepository;
import org.darktech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    public EmployeeProfile saveEmployee(EmployeeProfile employeeProfile) {
        if (employeeProfile.getUser() == null || employeeProfile.getUser().getId() == null) {
            throw new ResourceNotFoundException("User Not provided");
        }
        if (!employeeProfile.getUser().isActive()){
            throw  new UserNotFoundException("User not found with provided Id.");
        }
        User savingEmployee = userRepository.findById(employeeProfile.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        employeeProfile.setUser(savingEmployee);
        EmployeeProfile savedEmployee = employeeRepository.save(employeeProfile);
        return savedEmployee;
    }

    public EmployeeProfile getEmployee(Long id){
        if (id==null || id==0){
            throw new ResourceNotFoundException("User Id required");
        }

        return employeeRepository.findByUser_Id(id).get();
    }
}
