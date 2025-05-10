package com.example.Testing.System.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String groupName;

    @OneToMany(mappedBy = "student")
    private List<Ticket> tickets;

    @Column(name = "created_at")
    private Instant createdAt;
}

