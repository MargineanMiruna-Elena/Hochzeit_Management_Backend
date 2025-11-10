package com.example.backend.controller;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.service.AuthentificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AuthentificationService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody RegisterRequest request) {
        User savedUser = userService.registerUser(request);
        savedUser.setPassword(null); // don’t send password back
        return savedUser;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){
        User user = userService.login(request);
        return new AuthResponse(user.getId(), user.getName(),user.getEmail());
    }
}