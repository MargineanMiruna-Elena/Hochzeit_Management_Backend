package com.example.backend.service;

import com.example.backend.dto.CreateParticipantRequest;
import com.example.backend.dto.ParticipantResponse;
import com.example.backend.dto.UpdateParticipantRequest;

import java.util.List;

public interface ParticipantService {

    ParticipantResponse createParticipant(CreateParticipantRequest request);

    ParticipantResponse updateParticipant(Long participantId, UpdateParticipantRequest request);

    void deleteParticipant(Long participantId);

    ParticipantResponse getParticipant(Long participantId);

    List<ParticipantResponse> getParticipantsByEvent(Long eventId);

    List<ParticipantResponse> getAcceptedParticipantsByEvent(Long eventId);

    List<ParticipantResponse> getDeclinedParticipantsByEvent(Long eventId);

    List<ParticipantResponse> getPendingParticipantsByEvent(Long eventId);
}
