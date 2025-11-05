package com.example.backend.controller;

import com.example.backend.dto.CreateOrganizerRequest;
import com.example.backend.dto.CreateParticipantRequest;
import com.example.backend.dto.UpdateParticipantRequest;
import com.example.backend.dto.OrganizerResponse;
import com.example.backend.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizers")
@CrossOrigin(origins = "*")
public class OrganizerController {

    @Autowired
    private OrganizerService organizerService;

    // create new organizer
    @PostMapping
    public ResponseEntity<OrganizerResponse> createOrganizer(@RequestBody CreateOrganizerRequest request) {
        OrganizerResponse response = organizerService.createOrganizer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // get organizer by mail
    @GetMapping("/{mail}")
    public ResponseEntity<OrganizerResponse> getOrganizerByMail(@PathVariable String mail) {
        OrganizerResponse response = organizerService.getOrganizerByMail(mail);
        return ResponseEntity.ok(response);
    }

    // find if user is organizer
    @GetMapping("/user/{userId}")
    public ResponseEntity<OrganizerResponse> getOrganizerByUserId(@PathVariable Long userId) {
        OrganizerResponse organizer = organizerService.getOrganizerByUserId(userId);
        return ResponseEntity.ok(organizer);
    }

    //get all organizers
    @GetMapping
    public ResponseEntity<List<OrganizerResponse>> getAllOrganizers() {
        List<OrganizerResponse> organizers = organizerService.getAllOrganizers();
        return ResponseEntity.ok(organizers);
    }

    //delete all organizer
    @DeleteMapping("/{mail}")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable String mail) {
        organizerService.deleteOrganizerByMail(mail);
        return ResponseEntity.noContent().build();
    }


}