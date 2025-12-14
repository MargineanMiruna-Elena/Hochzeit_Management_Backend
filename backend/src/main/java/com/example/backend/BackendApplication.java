package com.example.backend;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.backend.enums.FoodPreference;
import com.example.backend.enums.TransportationMethod;
import com.example.backend.model.Event;
import com.example.backend.model.User;
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
//        try {
//            User testUser = new User();
//            testUser.setEmail("test@example.com");
//            testUser.setPassword("password123");
//            testUser.setName("Test User");
//
//            User savedUser = userRepository.save(testUser);
//            System.out.println("Created test user with ID: " + savedUser.getId());
//
//            Event testEvent = new Event();
//            testEvent.setName("Amelia and Ben's Wedding");
//            testEvent.setDescription("We are thrilled to have you celebrate with us on our special day!");
//            testEvent.setStartDate(LocalDate.now().plusDays(30));
//            testEvent.setEndDate(LocalDate.now().plusDays(30));
//            testEvent.setEmailOrg1("amelia@example.com");
//            testEvent.setEmailOrg2("ben@example.com");
//            testEvent.setLocationID(1123L);
//            testEvent.setCreatorId(savedUser.getId());
//
//            testEvent.setAskFoodPreferences(true);
//            testEvent.setAskTransportation(true);
//
//            testEvent.setAvailableFoodPreferences(new HashSet<>(Arrays.asList(
//                FoodPreference.VEGETARIAN,
//                FoodPreference.VEGAN,
//                FoodPreference.NO_PREFERENCE
//            )));
//
//            testEvent.setAvailableTransportationMethods(new HashSet<>(Arrays.asList(
//                TransportationMethod.CAR,
//                TransportationMethod.PUBLIC_TRANSPORT
//            )));
//
//            Event savedEvent = eventRepository.save(testEvent);
//
//            Participant testParticipant = new Participant();
//            testParticipant.setName("John Doe");
//            testParticipant.setEmail("vacax44100@besenica.com");
//            testParticipant.setEvent(savedEvent);
//            testParticipant.setAttending(false);
//
//            Participant savedParticipant = participantRepository.save(testParticipant);
//
//            invitationService.sendInvitations(savedEvent, Arrays.asList(savedParticipant));
//
//            System.out.println("Test invitation sent successfully!");
//            System.out.println("Event ID: " + savedEvent.getId());
//            System.out.println("Participant ID: " + savedParticipant.getId());
//            System.out.println("Check the console above for the invitation link with token!");
//
//        } catch (Exception e) {
//            System.err.println("Error creating test data: " + e.getMessage());
//            e.printStackTrace();
//        }
    }
}