package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    public Optional<Invitation> findByCode(UUID code);
}
