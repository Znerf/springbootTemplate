package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transaction management

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { // Constructor injection, preferred over @Autowired on field
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true) // Optimize for read operations
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional // For write operations
    public User createUser(User user) {
        // Add any business logic here before saving, e.g., validation
        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail()); // Ensure unique email if changing
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}