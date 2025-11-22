package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    private @Getter @ Setter String email;
    @Column(name = "password", nullable = false)
    private @Getter @Setter String password;
    @Column(name = "name", nullable = false)
    private @Getter @Setter String name;
    

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

}