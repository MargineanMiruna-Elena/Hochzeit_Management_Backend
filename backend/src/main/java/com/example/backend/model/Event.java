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
    private Long id;

    private @Getter @Setter String name;

    @Temporal(TemporalType.DATE)
    private @Getter @Setter Date startDate;

    @Temporal(TemporalType.DATE)
    private @Getter @Setter Date endDate;

    private @Getter @Setter String description;

    private @Getter @Setter String emailOrg1;

    private @Getter @Setter String emailOrg2;

    private @Getter @Setter Long locationID;

    public Event(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
