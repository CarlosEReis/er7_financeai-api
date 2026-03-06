package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.*;
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
    private final GroupMemberService groupMemberService;
    private final EmailService emailService;
    private final SharingService sharingService;
    private final TransactionService transactionService;
    private final GroupService groupService;

    public InvitationService(InvitationRepository invitationRepository, UserService userService,
                             GroupMemberService groupMemberServiceService, TransactionService transactionService,
                             SharingService sharingService,
                             EmailService emailService,
                             GroupService groupService) {
        this.invitationRepository = invitationRepository;
        this.userService = userService;
        this.groupMemberService = groupMemberServiceService;
        this.emailService = emailService;
        this.sharingService = sharingService;
        this.transactionService = transactionService;
        this.groupService = groupService;
    }

    @Transactional
    public Invitation create(String sub, String inviteeEmail, Integer groupId) {
        var host = userService.findBySub(sub);
        var invitee = userService.findByEmail(inviteeEmail);
        var group = groupService.findById(groupId);


        Invitation invitation = new Invitation(host, invitee, group);
        var invitationCreated = invitationRepository.save(invitation);

        emailService.sendEmail(
                host.getEmail(),
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
            //case ACCEPTED -> acceptedInvitation(invitation, inviteeSub);
            case REJECTED -> invitation.reject();
            default -> throw new RuntimeException("Invalid invitation status");
        }
        this.invitationRepository.save(invitation);
    }

    @Transactional
    public void acceptedInvitation(UUID invitationToken, String inviteeSub) {
        var invitation = findByCode(invitationToken);

        if (!invitation.getStatus().equals(InvitationStatus.PENDING))
            throw new IllegalStateException("Convite já foi processado (Status: " + invitation.getStatus() + ")");

        if (!inviteeSub.equals(invitation.getInvitee().getSub()))
            throw new IllegalStateException("Convidado do convite não é o mesmo do aceite");

        invitation.accept();
        invitationRepository.save(invitation);
        this.groupMemberService.create(invitation.getInvitee(), Paper.VISUALIZADOR, invitation.getGroup(), MemberStatus.ATIVO);
    }

}
