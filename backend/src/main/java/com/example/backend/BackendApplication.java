package com.example.backend;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.example.backend.model.Event;
import com.example.backend.model.Participant;
import com.example.backend.repository.EventRepository;
import com.example.backend.repository.ParticipantRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.InvitationService;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
    //     try {
    //         Event testEvent = new Event();
    //         testEvent.setName("Amelia and Ben's Wedding");
    //         testEvent.setDescription("We are thrilled to have you celebrate with us on our special day!");
    //         testEvent.setStartDate(LocalDate.now().plusDays(30));
    //         testEvent.setEndDate(LocalDate.now().plusDays(30));
    //         testEvent.setEmailOrg1("amelia@example.com");
    //         testEvent.setEmailOrg2("ben@example.com");
    //         testEvent.setLocationID(1123L);
    //         testEvent.setAskFoodPreferences(true);
            
    //         testEvent.setAskFoodPreferences(true);
            
    //         Event savedEvent = eventRepository.save(testEvent);

    //         Participant testParticipant = new Participant();
    //         testParticipant.setName("John Doe");
    //         testParticipant.setEmail("sesey86483@alexida.com");

    //         testParticipant.setEvent(savedEvent);
    //         testParticipant.setAttending(false);

    //         Participant savedParticipant = participantRepository.save(testParticipant);

    //         invitationService.sendInvitations(savedEvent, Arrays.asList(savedParticipant));

    //         System.out.println("Test invitation sent successfully!");
    //         System.out.println("Event ID: " + savedEvent.getId());
    //         System.out.println("Participant ID: " + savedParticipant.getId());
    //         System.out.println("Check the console above for the invitation link with token!");

    //     } catch (Exception e) {
    //         System.err.println("Error creating test data: " + e.getMessage());
    //         e.printStackTrace();
    //     }
     }
}