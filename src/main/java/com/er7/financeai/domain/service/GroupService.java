package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.Group;
import com.er7.financeai.domain.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository repository;

    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Group create() {
        var group = new Group();
        group.setName(UUID.randomUUID());
        return repository.save(group);
    }
}
