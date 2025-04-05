package org.darktech.entity;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "employee_profile")
public class EmployeeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    private String companyName;
    private String designation;
    private Long workExperience;
    private Long contactNumber;
    private Date dob;
    private String address;
    private String currentLocation;

    @Column(nullable = false)
    private String expUrl;

    @Column(nullable = true)
    private String profilePicture;

    public EmployeeProfile() {}

    public EmployeeProfile(User user, String companyName, String designation, Long workExperience, Long contactNumber, Date dob, String address, String currentLocation, String expUrl, String profilePicture) {
        this.user = user;
        this.companyName = companyName;
        this.designation = designation;
        this.workExperience = workExperience;
        this.contactNumber = contactNumber;
        this.dob = dob;
        this.address = address;
        this.currentLocation = currentLocation;
        this.expUrl = expUrl;
        this.profilePicture = profilePicture;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Long getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(Long workExperience) {
        this.workExperience = workExperience;
    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getExpUrl() {
        return expUrl;
    }

    public void setExpUrl(String expUrl) {
        this.expUrl = expUrl;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
