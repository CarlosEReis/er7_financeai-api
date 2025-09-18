package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.User;
import com.er7.financeai.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(User user) {
        repository.save(user);
    }

    public User findBySub(String sub) {
        return repository.findBySub(sub)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
