package com.er7.financeai.api;

import com.er7.financeai.api.model.request.AcceptInvitationRequest;
import com.er7.financeai.api.model.request.InvitationRequest;
import com.er7.financeai.domain.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody InvitationRequest invitationRequest, Authentication authentication) {
        var inviter = authentication.getName();
        var invitee = invitationRequest.email();
        var groupId = invitationRequest.groupId();

        invitationService.create(inviter, invitee, groupId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accept")
    public ResponseEntity<Void> accept(@RequestBody AcceptInvitationRequest token, Authentication authentication) {
        var invetee  = authentication.getName();
        invitationService.acceptedInvitation(token.token(), invetee);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reject")
    public ResponseEntity<Void> reject(@RequestBody AcceptInvitationRequest token, Authentication authentication) {
        var invetee  = authentication.getName();

        System.out.println("IMPLEMENTAR FLUXO DE REJEIÇÃO");

        //invitationService.invitationProcess(UUID.fromString(token.token()), InvitationStatus.REJECTED, invetee);
        return ResponseEntity.noContent().build();
    }

}
