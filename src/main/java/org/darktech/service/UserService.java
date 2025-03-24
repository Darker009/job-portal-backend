package org.darktech.service;

import org.darktech.dto.UserDTO;
import org.darktech.entity.User;
import org.darktech.exception.ResourceNotFoundException;
import org.darktech.exception.UserNotFoundException;
import org.darktech.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Saves a new user with an encoded password.
     *
     * @param user the user to be saved
     * @return the saved user
     */
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Validates user credentials for login.
     *
     * @param email    user's email
     * @param password raw password input
     * @return a UserDTO if authentication is successful
     * @throws ResourceNotFoundException if email is invalid, password is incorrect, or user is inactive
     */
    public UserDTO userLogin(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Email or Password"));

        if (!user.isActive()) {
            throw new ResourceNotFoundException("User is inactive. Please contact Admin");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
        }
        throw new ResourceNotFoundException("Invalid Password, Try Again");
    }

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return the User or null if not found
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Retrieves user details as a DTO.
     *
     * @param id the user id
     * @return a UserDTO representing the user
     * @throws UserNotFoundException if the user is not found or is inactive
     */
    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with Id: " + id));

        if (!user.isActive()) {
            throw new UserNotFoundException("User Id " + id + " is deactivated. Reach out to admin for activation.");
        }

        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    /**
     * Updates user details for an existing user.
     * Fields will be updated only if a non-empty value is provided.
     *
     * @param id          the id of the user to update
     * @param updatedUser a User object containing updated fields
     * @return a success message upon update
     * @throws UserNotFoundException if the user is not found or is inactive
     */
    @Transactional
    public String updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with Id: " + id));

        if (!existingUser.isActive()) {
            throw new UserNotFoundException("User Id: " + id + " is deactivated. Reach out to admin for activation.");
        }

        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().isEmpty()) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null && !updatedUser.getLastName().isEmpty()) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getRole() != null && !updatedUser.getRole().isEmpty()) {
            existingUser.setRole(updatedUser.getRole());
        }

        userRepository.save(existingUser);
        return "User updated successfully";
    }

    /**
     * Deactivates a user by setting their active status to false.
     *
     * @param id the id of the user to deactivate
     * @return a message indicating the deactivation status
     */
    public String deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Id not found"));

        if (user.isActive()) {
            user.setActive(false);
            userRepository.save(user);
            return "User deactivated successfully. Reach out to admin for activation.";
        } else {
            return "User is already deactivated. Please contact admin for reactivation if needed.";
        }
    }
}
