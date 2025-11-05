package com.example.backend.service;

import com.example.backend.dto.CreateOrganizerRequest;
import com.example.backend.dto.CreateParticipantRequest;
import com.example.backend.dto.UpdateParticipantRequest;
import com.example.backend.dto.OrganizerResponse;
import com.example.backend.model.Organizer;
import com.example.backend.model.User;
import com.example.backend.model.Event;
import com.example.backend.repository.OrganizerRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizerService {
    private OrganizerRepository organizerRepository;
    private UserRepository userRepository;

    // create new organizer
    public OrganizerResponse createOrganizer(CreateOrganizerRequest createOrganizerRequest) {
        if(organizerRepository.existsByMail(createOrganizerRequest.getMail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already organizer");
        }

        if (organizerRepository.existsByUserId(createOrganizerRequest.getUserId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already organizer");
        }

        User user = userRepository.findById(createOrganizerRequest.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Organizer organizer = new Organizer();
        organizer.setMail(createOrganizerRequest.getMail());
        organizer.setUser(user);

        try{
            Organizer savedOrganizer = organizerRepository.save(organizer);
            return new OrganizerResponse(
                    savedOrganizer.getMail(),
                    savedOrganizer.getUser().getId(),
                    savedOrganizer.getUser().getName()
            );
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create organizer", e);
        }
    }

    //get organizer by mail

    public OrganizerResponse getOrganizerByMail(String mail){
        Organizer organizer = organizerRepository.findById(mail).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found"));
        return new OrganizerResponse(
                organizer.getMail(),
                organizer.getUser().getId(),
                organizer.getUser().getName()
        );
    }

    // get organizer by user
    public OrganizerResponse getOrganizerByUserId (Long userId){
        Organizer organizer = organizerRepository.findByUserId(userId).orElseThrow(()  -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found"));
        return new OrganizerResponse(
                organizer.getMail(),
                organizer.getUser().getId(),
                organizer.getUser().getName()
        );
    }

    //delete by mail

    public void deleteOrganizerByMail(String mail){
        if(!organizerRepository.existsByMail(mail)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found");
        }

        try{
            organizerRepository.deleteById(mail);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete organizer",e);
        }
    }

    public List<OrganizerResponse> getAllOrganizers() {
        List<Organizer> organizers = organizerRepository.findAll();

        return organizers.stream()
                .map(org -> new OrganizerResponse(
                        org.getMail(),
                        org.getUser().getId(),
                        org.getUser().getName()
                ))
                .collect(Collectors.toList());
    }



}
