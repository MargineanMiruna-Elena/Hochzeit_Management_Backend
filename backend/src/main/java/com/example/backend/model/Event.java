package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private @Getter @Setter String name;

    @Temporal(TemporalType.DATE)
    @Column(name ="start_date", nullable = false)
    private @Getter @Setter LocalDate startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date", nullable = false)
    private @Getter @Setter LocalDate endDate;

    @Column(name = "description", nullable = false)
    private @Getter @Setter String description;

    @Column(name = "email_org1", nullable = false, updatable = false)
    private @Getter @Setter String emailOrg1;

    @Column(name = "email_org2", nullable = false, updatable = false)
    private @Getter @Setter String emailOrg2;

    @Column(name = "location_id", nullable = false)
    private @Getter @Setter Long locationID;

    @Column(name = "creator_id", nullable = false)
    private @Getter @Setter Long creatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private @Getter @Setter User creator;

    @Column(name = "image_url", nullable = true)
    private @Getter @Setter String imageUrl;

    public Event(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }



}
