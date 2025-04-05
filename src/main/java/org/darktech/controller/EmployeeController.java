package org.darktech.controller;

import org.darktech.common.CommonFunction;
import org.darktech.entity.EmployeeProfile;
import org.darktech.entity.User;
import org.darktech.service.EmployeeService;
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
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonFunction commonFunction;

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<?> saveEmployee(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("companyName") String companyName,
            @RequestParam("designation") String designation,
            @RequestParam("workExperience") Long workExperience,
            @RequestParam("contactNumber") Long contactNumber,
            @RequestParam("dob") String dob,
            @RequestParam("address") String address,
            @RequestParam("currentLocation") String currentLocation,
            @RequestParam(value = "expUrl", required = true)MultipartFile expUrl) {

        if (userDetails == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String expFile;
        try {
          expFile = commonFunction.processFileUpload(expUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        EmployeeProfile employeeProfile = new EmployeeProfile();
        employeeProfile.setUser(user);
        employeeProfile.setCompanyName(companyName);
        employeeProfile.setDesignation(designation);
        employeeProfile.setWorkExperience(workExperience);
        employeeProfile.setContactNumber(contactNumber);

        try{
            employeeProfile.setDob(java.sql.Date.valueOf(dob));
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format for dob. Use YYYY-MM-DD"));
        }

        employeeProfile.setAddress(address);
        employeeProfile.setCurrentLocation(currentLocation);
        employeeProfile.setExpUrl(expFile);
        try {
            EmployeeProfile savedEmployee = employeeService.saveEmployee(employeeProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to save Employee profile"));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<EmployeeProfile> getEmployee(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<EmployeeProfile> employeeProfile = employeeService.getEmployee(user.getId());
        return employeeProfile
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PostMapping("/profile/picture")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<?> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("profilePicture") MultipartFile file){
        if (userDetails == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            String pictureUrl = commonFunction.processFileUpload(file);
            Optional<EmployeeProfile> optProfile = employeeService.getEmployee(user.getId());
            if (optProfile.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Employee profile Not found"));
            }
            EmployeeProfile employeeProfile = optProfile.get();
            employeeProfile.setProfilePicture(pictureUrl);
            EmployeeProfile updatedProfile = employeeService.saveEmployee(employeeProfile);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload profile picture"));
        }

    }
}
