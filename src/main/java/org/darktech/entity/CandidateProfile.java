package org.darktech.entity;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "candidate_profile")
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    private String collegeName;
    private String degree;
    private String specialization;
    private Long contactNumber;
    private String skills;
    private Date dob;
    private String address;
    private String currentLocation;

    @Column(nullable = true)
    private String resumeUrl;

    @Column(nullable = true)
    private String profilePicture;

    public CandidateProfile() {}

    public CandidateProfile(User user, String collegeName, String degree, String specialization,
                            Long contactNumber, String skills, Date dob, String address,
                            String currentLocation, String resumeUrl, String profilePicture) {
        this.user = user;
        this.collegeName = collegeName;
        this.degree = degree;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.skills = skills;
        this.dob = dob;
        this.address = address;
        this.currentLocation = currentLocation;
        this.resumeUrl = resumeUrl;
        this.profilePicture = profilePicture;
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

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
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

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
