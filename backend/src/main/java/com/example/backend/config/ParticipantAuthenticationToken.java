package com.example.backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.example.backend.model.Event;
import com.example.backend.model.Participant;

import java.util.Collection;
import java.util.Collections;

public class ParticipantAuthenticationToken implements Authentication {
    
    private final Participant participant;
    private final Event event;
    private boolean authenticated = true;

    public ParticipantAuthenticationToken(Participant participant, Event event) {
        this.participant = participant;
        this.event = event;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return event;
    }

    @Override
    public Object getPrincipal() {
        return participant;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return participant.getEmail();
    }
    
    public Participant getParticipant() {
        return participant;
    }
    
    public Event getEvent() {
        return event;
    }
}