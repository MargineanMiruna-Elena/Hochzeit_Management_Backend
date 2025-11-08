package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

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

    public Event(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
