package com.example.backend.controller;


import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.service.AuthentificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.AuthResponse;
import com.example.backend.repository.UserRepository;
import com.example.backend.config.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AuthentificationService userService;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private HandlerMapping resourceHandlerMapping;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User savedUser = userService.registerUser(request);
            savedUser.setPassword(null); // don’t send password back
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password("")
                    .authorities(Collections.emptyList())
                    .build();

            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    token
            ));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PutMapping("/change-password/{userId}")
    public void changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
    }

}