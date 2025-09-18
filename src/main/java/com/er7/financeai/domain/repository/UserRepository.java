package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u join fetch u.group WHERE u.sub = :sub")
    public Optional<User> findBySub(String sub);
    public Optional<User> findByEmail(String email);
}
