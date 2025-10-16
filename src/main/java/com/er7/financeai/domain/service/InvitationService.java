package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.Invitation;
import com.er7.financeai.domain.model.InvitationStatus;
import com.er7.financeai.domain.repository.InvitationRepository;
import com.er7.financeai.infra.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final EmailService emailService;
    private final TransactionService transactionService;

    public InvitationService(InvitationRepository invitationRepository, UserService userService,
                             GroupService groupService, TransactionService transactionService,
                             EmailService emailService) {
        this.invitationRepository = invitationRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.emailService = emailService;
        this.transactionService = transactionService;
    }

    @Transactional
    public Invitation create(String sub, String inviteeEmail) {
        var inviter = userService.findBySub(sub);
        var invitee = userService.findByEmail(inviteeEmail);
        Invitation invitation = new Invitation(inviter, invitee, groupService.create());
        var invitationCreated = invitationRepository.save(invitation);

        emailService.sendEmail(
                inviter.getEmail(),
                List.of(invitee.getEmail()),
                invitationCreated.getCode().toString());
        return invitationCreated;
    }

    public Invitation findByCode(UUID code) {
        return invitationRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
    }

    @Transactional
    public void invitationProcess(UUID invitationCode, InvitationStatus status, String inviteeSub) {
        var invitation = findByCode(invitationCode);
        switch (status) {
            case ACCEPTED -> acceptedInvitation(invitation, inviteeSub);
            case REJECTED -> invitation.reject();
            default -> throw new RuntimeException("Invalid invitation status");
        }
        this.invitationRepository.save(invitation);
    }

    private void acceptedInvitation(Invitation invitation, String inviteeSub) {
        var inviteeInvitationDb = userService.findById(invitation.getInvitee().getId());
        if (!inviteeSub.equals(inviteeInvitationDb.getSub()))
            throw new IllegalStateException("Convidado do convite não é o mesmo do aceite");

        invitation.accept();
        var inviter = userService.findById(invitation.getInviter().getId());

        inviter.setGroup(invitation.getGroup());
        inviteeInvitationDb.setGroup(invitation.getGroup());
        userService.save(inviter);
        userService.save(inviteeInvitationDb);
        transactionService.updateTransactionsToGroup(invitation.getGroup().getId(), List.of(invitation.getInviter().getId(), invitation.getInvitee().getId()));
    }

}
