package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private @Getter @Setter String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private @Getter @Setter Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date", nullable = false)
    private @Getter @Setter Date endDate;

    @Column(name = "description", nullable = false)
    private @Getter @Setter String description;

    @Column(name = "email_org1", nullable = false)
    private @Getter @Setter String emailOrg1;

    @Column(name = "email_org2", nullable = false)
    private @Getter @Setter String emailOrg2;

    @Column(name = "location_id", nullable = false)
    private @Getter @Setter Long locationID;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    public Event(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event(String name,
                 Date startDate,
                 Date endDate,
                 String description,
                 String emailOrg1,
                 String emailOrg2,
                 Long locationID) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.emailOrg1 = emailOrg1;
        this.emailOrg2 = emailOrg2;
        this.locationID = locationID;
    }

    public Event(String name,
                 Date startDate,
                 Date endDate,
                 String description,
                 String emailOrg1,
                 String emailOrg2,
                 Long locationID,
                 List<Participant> participants) {
        this(name, startDate, endDate, description, emailOrg1, emailOrg2, locationID);
        this.participants = participants == null ? new ArrayList<>() : participants;
        for (Participant p : this.participants) {
            p.setEvent(this); 
        }
    }



}
