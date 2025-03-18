package org.darktech.controller;

import org.darktech.entity.User;
import org.darktech.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public User saveUser(@RequestBody User user){
        User savedUser = userService.saveUser(user);
        return savedUser;
    }

    @GetMapping("/fetch/{id}")
    public User getUser(@PathVariable Long id){

        return  userService.getUser(id);
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user){
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/delete/{id}")
    public String deactivateUser(@PathVariable Long id){
        return userService.deactivateUser(id);
    }



}
