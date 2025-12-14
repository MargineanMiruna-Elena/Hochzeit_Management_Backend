package com.example.backend.config;

import com.example.backend.model.ParticipantToken;
import com.example.backend.service.ParticipantTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class ParticipantTokenFilter extends OncePerRequestFilter {

    private final ParticipantTokenService participantTokenService;

    public ParticipantTokenFilter(ParticipantTokenService participantTokenService) {
        this.participantTokenService = participantTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        
        // Only handle invitation endpoints
        if (!path.startsWith("/api/invitation")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println(">>> ParticipantTokenFilter: " + request.getMethod() + " " + request.getRequestURI());

        String token = request.getParameter("token");
        
        if (token == null) {
            // Check if token is in request body for POST requests
            if ("POST".equals(request.getMethod())) {
                // Let it pass - the controller will handle the token validation
                filterChain.doFilter(request, response);
                return;
            }
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Missing invitation token\"}");
            return;
        }
        
        Optional<ParticipantToken> participantToken = participantTokenService.validateToken(token);
        
        if (participantToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired invitation token\"}");
            return;
        }
        
        // Create special authentication for participant
        ParticipantAuthenticationToken authToken = new ParticipantAuthenticationToken(
            participantToken.get().getParticipant(),
            participantToken.get().getEvent()
        );
        
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("Participant authentication set for: " + participantToken.get().getParticipant().getEmail());
        
        filterChain.doFilter(request, response);
    }
}