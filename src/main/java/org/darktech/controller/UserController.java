package org.darktech.controller;

import org.darktech.dto.UserDTO;
import org.darktech.entity.User;
import org.darktech.exception.ResourceNotFoundException;
import org.darktech.security.JwtUtils;
import org.darktech.service.UserDetailsServiceImpl;
import org.darktech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public User saveUser(@RequestBody User user){

        User savedUser = userService.saveUser(user);
        return savedUser;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody User user) {

        if (user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and Password are required!"));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            UserDTO userDTO = userService.userLogin(user.getEmail(), user.getPassword());
            return ResponseEntity.ok(Map.of("token", jwt, "user", userDTO));

        } catch (ResourceNotFoundException err) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", err.getMessage()));
        } catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", err.getMessage()));
        }
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        if(id==null || id==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide valid user Id");
        }
        UserDTO userDTO =userService.getUser(id);
        return  ResponseEntity.status(HttpStatus.OK).body(Map.of("UserData",userDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user){
        if (id==0 && id==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user ID");
        }
       String message = userService.updateUser(id, user) ;
       if(message.contains("successfully")){
           return ResponseEntity.status(HttpStatus.CREATED).body("User updated successfully");
       }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User update failed");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        if (id == null || id == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide valid user Id");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.deactivateUser(id));
    }





}
