package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findAllByOwnerId(Long userId);
}
