package org.darktech.service;

import jakarta.persistence.EntityNotFoundException;
import org.darktech.entity.User;
import org.darktech.exception.UserNotFoundException;
import org.darktech.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public User getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User not found with Id: "+ id));
        if(!user.isActive()){
            throw new UserNotFoundException("User Id " + id +" is Deactivated Reach to admin for activation");

        }
        return user;
    }

    public User updateUser(Long id, User updatedUser){

        User existingUser = userRepository.findById(id).get();

        if(updatedUser.getFirstName()!=null && !updatedUser.getFirstName().isEmpty())
        {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if(updatedUser.getLastName()!=null && !updatedUser.getLastName().isEmpty()){
            existingUser.setLastName(updatedUser.getLastName());
        }
        if(updatedUser.getPassword()!=null && !updatedUser.getPassword().isEmpty()){
            existingUser.setPassword(updatedUser.getPassword());
        }
        return userRepository.save(existingUser);
    }

    public String deactivateUser(Long id){
        User user = userRepository.findById(id).get();
        if(user.isActive()==true){
            boolean active = false;
            user.setActive(active);
        }
        userRepository.save(user);
        return "Reach to admin for activation";
    }


}
