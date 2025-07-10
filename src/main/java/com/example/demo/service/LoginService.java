package com.example.demo.service;

import com.example.demo.model.Login;
import com.example.demo.repository.LoginRepository;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LoginService {
    private final LoginRepository loginRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // @Autowired
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(readOnly = true) 
    public List<Login> getAllLogins() {
        return loginRepository.findAll();
    }

    @Transactional(readOnly = true) 
    public Optional<Login> getLoginById(Long id) {
        return loginRepository.findById(id);
    }

    @Transactional(readOnly = true) 
    public Optional<Login> getLoginByEmail(String email) {
        return loginRepository.findByEmail(email);
    }

    @Transactional
    public Login createLogin(Login login) {
        login.setPassword(passwordEncoder.encode(login.getPassword()));
        return loginRepository.save(login);
    }

    @Transactional
    public Login updateLogin(Long id, Login updatedLogin) {
        return loginRepository.findById(id).map(existing -> {
            existing.setFirstName(updatedLogin.getFirstName());
            existing.setLastName(updatedLogin.getLastName());
            existing.setEmail(updatedLogin.getEmail());
            if (updatedLogin.getPassword() != null && !updatedLogin.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(updatedLogin.getPassword()));
            }
            existing.setDateOfBirth(updatedLogin.getDateOfBirth());
            return loginRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Login not found"));
    }

    @Transactional
    public void deleteLogin(Long id) {
        loginRepository.deleteById(id);
    }

    @Transactional(readOnly = true)  
    public boolean authenticate(String email, String rawPassword) {
        Optional<Login> userOpt = loginRepository.findByEmail(email);
        return userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
    }
} 