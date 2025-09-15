package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findBySub(String sub);
}
