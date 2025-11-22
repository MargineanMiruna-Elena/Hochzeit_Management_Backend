package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private @Getter @Setter String name;

    @Column(name = "address", nullable = false)
    private @Getter @Setter String address;

    public Location(String name, String address) {
        this.name = name;
        this.address = address;
    }
}