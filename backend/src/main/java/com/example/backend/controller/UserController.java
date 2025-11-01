package com.example.backend.controller;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody RegisterRequest request) {
        User savedUser = userService.registerUser(request);
        savedUser.setPassword(null); // don’t send password back
        return savedUser;
    }
}