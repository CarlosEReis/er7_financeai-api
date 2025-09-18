package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.Invitation;
import com.er7.financeai.domain.model.InvitationStatus;
import com.er7.financeai.domain.repository.InvitationRepository;
import com.er7.financeai.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final TransactionService transactionService;

    public InvitationService(InvitationRepository invitationRepository, UserService userService, GroupService groupService, TransactionRepository transactionRepository, TransactionService transactionService) {
        this.invitationRepository = invitationRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.transactionService = transactionService;
    }

    @Transactional
    public Invitation create(String sub, String inviteeEmail) {
        Invitation invitation = new Invitation(
            userService.findBySub(sub),
            userService.findByEmail(inviteeEmail),
            groupService.create());
        return invitationRepository.save(invitation);
    }

    public Invitation findByCode(UUID code) {
        return invitationRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Invitation not found"));
    }

    @Transactional
    public void invitationProcess(UUID invitationCode, InvitationStatus status) {
        var invitation = findByCode(invitationCode);
        switch (status) {
            case ACCEPTED -> {
                invitation.accept();
                var inviter = userService.findById(invitation.getInviter().getId());
                var invitee = userService.findById(invitation.getInvitee().getId());
                inviter.setGroup(invitation.getGroup());
                invitee.setGroup(invitation.getGroup());
                userService.save(inviter);
                userService.save(invitee);
                transactionService.updateTransactionsToGroup(invitation.getGroup().getId(), List.of(invitation.getInviter().getId(), invitation.getInvitee().getId()));
            }
            case REJECTED -> invitation.reject();
            default -> throw new RuntimeException("Invalid invitation status");
        }
        this.invitationRepository.save(invitation);
    }

}
