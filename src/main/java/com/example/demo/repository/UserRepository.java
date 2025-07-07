package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA automatically provides methods like save(), findById(), findAll(), deleteById()

    // Example of a custom query method by property:
    Optional<User> findByEmail(String email);
}