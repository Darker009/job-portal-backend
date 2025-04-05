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


    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


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


    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with Id: " + id));

        if (!user.isActive()) {
            throw new UserNotFoundException("User Id " + id + " is deactivated. Reach out to admin for activation.");
        }

        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }


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
