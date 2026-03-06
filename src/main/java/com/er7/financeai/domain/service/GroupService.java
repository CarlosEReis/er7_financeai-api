package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.Group;
import com.er7.financeai.domain.model.MemberStatus;
import com.er7.financeai.domain.model.Paper;
import com.er7.financeai.domain.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberService groupMemberService;
    private final UserService userService;

    public GroupService(GroupRepository groupRepository, GroupMemberService groupMemberService, UserService userService) {
        this.groupRepository = groupRepository;
        this.groupMemberService = groupMemberService;
        this.userService = userService;
    }

    public Group create(String ownerSubscription,Group group) {
        var owner = userService.findBySub(ownerSubscription);
        group.setOwner(owner);
        var groupCreated = groupRepository.save(group);
        if (groupCreated != null) {
            groupMemberService.create(owner, Paper.ADMIN, groupCreated, MemberStatus.ATIVO);
        }
        return groupRepository.save(group);
    }

    public Group findById(Integer groupId) {
        return groupRepository.findById(groupId).get();
    }

    public List<Group> findAllByUser(String userSub) {
        var owner = userService.findBySub(userSub);
        return this.groupRepository.findAllByOwnerId(owner.getId());
    }
}
