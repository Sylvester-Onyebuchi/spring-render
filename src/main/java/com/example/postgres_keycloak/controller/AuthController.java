package com.example.postgres_keycloak.controller;


import com.example.postgres_keycloak.dto.UserRequest;
import com.example.postgres_keycloak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        userService.save(userRequest);
        return ResponseEntity.ok("User successfully created");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestParam UUID userId) {
        userService.delete(userId);
        return ResponseEntity.ok("User successfully created");
    }


}
