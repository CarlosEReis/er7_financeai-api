package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.*;
import com.er7.financeai.domain.repository.GroupMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupMemberService {

    private final GroupMemberRepository repository;

    public GroupMemberService(GroupMemberRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GroupMember create(User member, Paper paper, Group group, MemberStatus memberStatus) {
        var groupMember = new GroupMember();
        groupMember.setMember(member);
        groupMember.setPaper(paper);
        groupMember.setGroup(group);
        groupMember.setStatus(memberStatus);
        return repository.save(groupMember);
    }

    public GroupMember findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
