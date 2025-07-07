package com.example.demo.model;

import jakarta.persistence.*; // Use jakarta.persistence for Spring Boot 3+
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users") // Explicitly define the table name, good practice
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses database's identity column (e.g., BIGSERIAL in Postgres)
    private Long id;

    @Column(name = "first_name", nullable = false) // Explicit column mapping and constraints
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false) // Ensure email is unique and not null
    private String email;

    // @Version
    // private Long version;
    // You can add more fields as needed, e.g.,
    // @Column(name = "created_at")
    // private LocalDateTime createdAt;
}