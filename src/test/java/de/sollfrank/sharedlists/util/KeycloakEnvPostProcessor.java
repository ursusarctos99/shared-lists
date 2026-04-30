package de.sollfrank.sharedlists.util;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class KeycloakEnvPostProcessor implements EnvironmentPostProcessor {

    private static final String REALM = "shared-lists";
    private static final String CLIENT_SECRET = "shared-lists-secret";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        KeycloakContainer keycloakContainer =
            new KeycloakContainer("keycloak/keycloak:26.5.5")
                .withAdminUsername("admin")
                .withAdminPassword("admin")
                .withRealmImportFile("realm-shared-lists.json");

        keycloakContainer.start();

        createInitialUsers(keycloakContainer.getKeycloakAdminClient());

        String base = keycloakContainer.getAuthServerUrl().replaceAll("/+$", "");
        String issuerUri = base + "/realms/" + REALM;

        environment.getPropertySources().addFirst(new MapPropertySource("test-keycloak", Map.of(
            "spring.security.oauth2.client.registration.keycloak.client-id", "shared-lists",
            "spring.security.oauth2.client.registration.keycloak.client-secret", CLIENT_SECRET,
            "spring.security.oauth2.client.provider.keycloak.issuer-uri", issuerUri
        )));
    }

    private void createInitialUsers(Keycloak adminClient) {
        createUser(adminClient, "franz", "franz@test.de", "Franz", "Maier");
        createUser(adminClient, "fritz", "fritz@test.de", "Fritz", "Bauer");
        adminClient.close();
    }

    private void createUser(Keycloak adminClient, String username, String email,
                            String firstName, String lastName) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailVerified(true);
        user.setEnabled(true);

        Response response = adminClient.realm(REALM).users().create(user);
        String[] parts = response.getHeaderString("location").split("/");
        String id = parts[parts.length - 1];
        response.close();

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue("Hallo123");
        credentials.setTemporary(false);

        adminClient.realm(REALM).users().get(id).resetPassword(credentials);
    }
}