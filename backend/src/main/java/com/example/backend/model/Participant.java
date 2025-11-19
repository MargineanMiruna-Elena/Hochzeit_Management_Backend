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
    @Column(name = "attending", nullable = false)
    private @Getter @Setter Boolean attending;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Participant(String name, String email, Boolean attending){
        this.name = name;
        this.email = email;
        this.attending = attending;
    }
}
    