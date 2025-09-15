package com.er7.financeai.api;

import com.er7.financeai.domain.model.User;
import com.er7.financeai.domain.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody User user) {
        service.save(user);
        return ResponseEntity.noContent().build();
    }
}