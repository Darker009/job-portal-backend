package org.darktech.controller;

import org.darktech.entity.EmployeeProfile;
import org.darktech.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/profile")
    public ResponseEntity<?> saveEmployee(@RequestBody EmployeeProfile employeeProfile){
        if (employeeProfile.getCompanyName()==null || employeeProfile.getCompanyName().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Company Name is required");
        }
        if (employeeProfile.getDesignation()==null || employeeProfile.getDesignation().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Job Title is required.");
        }
        EmployeeProfile savedEmployee = employeeService.saveEmployee(employeeProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getEmployee(@PathVariable Long userId){
        EmployeeProfile employeeProfile = employeeService.getEmployee(userId);
        return ResponseEntity.status(HttpStatus.OK).body(employeeProfile);
    }
}
