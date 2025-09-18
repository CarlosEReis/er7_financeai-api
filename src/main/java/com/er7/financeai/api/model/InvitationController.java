package com.er7.financeai.api.model;

import com.er7.financeai.api.model.request.InvitationRequest;
import com.er7.financeai.domain.model.InvitationStatus;
import com.er7.financeai.domain.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody InvitationRequest invitationRequest, Authentication authentication) {
        var inviterSub = authentication.getName();
        invitationService.create(inviterSub, invitationRequest.email());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{invitationCode}/accept")
    public ResponseEntity<Void> accept(@PathVariable String invitationCode) {
        invitationService.invitationProcess(UUID.fromString(invitationCode), InvitationStatus.ACCEPTED);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{invitationCode}/reject")
    public ResponseEntity<Void> reject(@PathVariable String invitationCode) {
        invitationService.invitationProcess(UUID.fromString(invitationCode), InvitationStatus.REJECTED);
        return ResponseEntity.noContent().build();
    }

}
