package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private @Getter @Setter String name;

    @Column(name = "email", nullable = false)
    private @Getter @Setter String email;

    @Column(name = "attending", nullable = true)
    private @Getter @Setter Boolean attending;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private @Getter @Setter Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private @Getter @Setter User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_preference", nullable = true)
    private @Getter @Setter MenuPreference menuPreference;

    public Participant(String name, String email, Boolean attending) {
        this.name = name;
        this.email = email;
        this.attending = attending;
    }

    public Participant(String name, String email, Boolean attending, Event event) {
        this.name = name;
        this.email = email;
        this.attending = attending;
        this.event = event;
    }
}