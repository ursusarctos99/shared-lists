package de.sollfrank.sharedlists;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

public class SharedListsSession extends WebSession {

    public SharedListsSession(Request request) {
        super(request);
    }

    public Optional<SimpleUser> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        if (auth.getPrincipal() instanceof OidcUser oidcUser) {
            return Optional.of(SimpleUser.from(oidcUser));
        }
        return Optional.empty();
    }

    public boolean isSignedIn() {
        return getUser().isPresent();
    }

    public static SharedListsSession get() {
        return (SharedListsSession) Session.get();
    }
}