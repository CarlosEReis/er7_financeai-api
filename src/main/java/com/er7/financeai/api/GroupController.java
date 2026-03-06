package com.er7.financeai.api;

import com.er7.financeai.api.model.request.GroupRequest;
import com.er7.financeai.domain.model.Group;
import com.er7.financeai.domain.service.GroupService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupservice) {
        this.groupService = groupservice;
    }

    @PostMapping
    public ResponseEntity<Group> create(@Valid @RequestBody GroupRequest groupRequest, Authentication authentication) {

        var group = new Group();
        group.setName(groupRequest.name());
        group.setDescription(groupRequest.description());
        var groupCreated = groupService.create(authentication.getName(), group);
        return ResponseEntity.ok(groupCreated);
    }

    @GetMapping
    public ResponseEntity<List<Group>> listAll(Authentication autentication){
        return ResponseEntity.ok(this.groupService.findAllByUser(autentication.getName()));
    }
}
