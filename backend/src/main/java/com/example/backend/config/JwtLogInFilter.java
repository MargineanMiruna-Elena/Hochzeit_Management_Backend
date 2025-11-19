package com.example.backend.config;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtLogInFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtLogInFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println(">>> JwtLogInFilter: " + request.getMethod() + " " + request.getRequestURI());

        String path = request.getServletPath();
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header = " + authHeader);

        // dacă nu e Bearer, lăsăm requestul să treacă mai departe NEAUTENTIFICAT
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer token, skipping JWT auth.");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username = null;
        try {
            username = jwtService.extractUsername(jwt);
            System.out.println("Username from token = " + username);
        } catch (Exception e) {
            System.out.println("Failed to parse token: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            User user = userRepository.findByEmail(username).orElse(null);
            if (user == null) {
                System.out.println("No user found in DB for email = " + username);
            } else {
                UserDetails userDetails = buildUserDetails(user);
                boolean valid = jwtService.isTokenValid(jwt, userDetails);
                System.out.println("Token valid for this user? " + valid);

                if (valid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication set in SecurityContext.");
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
