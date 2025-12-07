package com.example.backend.controller;


import com.example.backend.dto.*;
import com.example.backend.model.User;
import com.example.backend.service.AuthentificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.backend.repository.UserRepository;
import com.example.backend.config.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

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
    @Autowired
    private UserRepository userRepository;

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

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody UpdateUserRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", errors));
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loggedUserEmail = authentication.getName();

            System.out.println("DEBUG: loggedUserEmail = " + loggedUserEmail);  // ✅ Add this
            System.out.println("DEBUG: authentication = " + authentication);    // ✅ Add this

            User currentUser = userRepository.findByEmail(loggedUserEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            UpdateUserResponse response = userService.updateUserProfile(currentUser.getId(), request);

            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update profile"));
        }
    }

}