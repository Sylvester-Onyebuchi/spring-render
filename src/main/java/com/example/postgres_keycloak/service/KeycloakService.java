package com.example.postgres_keycloak.service;

import com.example.postgres_keycloak.config.KeycloakConfig;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;
    private final KeycloakConfig keycloakConfig;

    @Value("${keycloak.realm}")
    private String realm;

    public String createUser(String username, String email, String password, String roleName) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setCreatedTimestamp(System.currentTimeMillis());
        user.setCredentials(List.of(credential));

        Response response = keycloak.realm(realm)
                .users()
                .create(user);

        String userId = CreatedResponseUtil.getCreatedId(response);
        assignRole(userId, roleName);
        log.info("User {} created", response);
        return userId;
    }

    private void assignRole(String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }

    public void deleteUser(String userId) {
        keycloak.realm(realm)
                .users()
                .get(userId)
                .remove();

    }
}
