package com.er7.financeai.domain.repository;

import com.er7.financeai.domain.model.Group;
import com.er7.financeai.domain.model.GroupMember;
import com.er7.financeai.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    //Optional<Group> findByOwner(User owner);
}
