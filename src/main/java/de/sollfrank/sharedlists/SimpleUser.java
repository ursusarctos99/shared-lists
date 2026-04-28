package de.sollfrank.sharedlists;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public record SimpleUser(String id, String username, String email, String displayName) {

    public static SimpleUser from(OidcUser oidcUser) {
        String display = oidcUser.getFullName() != null
                ? oidcUser.getFullName()
                : oidcUser.getPreferredUsername();
        return new SimpleUser(
                oidcUser.getSubject(),
                oidcUser.getPreferredUsername(),
                oidcUser.getEmail(),
                display
        );
    }
}