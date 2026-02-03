package com.example.postgres_keycloak.service;


import com.example.postgres_keycloak.entity.Roles;
import com.example.postgres_keycloak.entity.User;
import com.example.postgres_keycloak.repository.UserRepository;
import com.example.postgres_keycloak.dto.UserRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;


    @Transactional
    public void save(UserRequest request){

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .roles(Collections.singleton(Roles.USER))
                .build();
        userRepository.save(newUser);


    }

    @Transactional
    public void delete(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        keycloakService.deleteUser(user.getKeycloakId());
        userRepository.deleteById(userId);


    }
}
