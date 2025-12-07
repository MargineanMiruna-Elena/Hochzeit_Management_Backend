package com.example.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestSecureController {

    @GetMapping("/some-secured-endpoint")
    public String test(Authentication authentication) {
        return "Hello, your JWT works! User = " +
                (authentication != null ? authentication.getName() : "null");
    }
}
