package de.sollfrank.sharedlists.config;

import de.sollfrank.sharedlists.SimpleUser;
import de.sollfrank.sharedlists.model.User;
import de.sollfrank.sharedlists.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OidcLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OidcLoginSuccessHandler(UserRepository userRepository) {
        super("/");
        setAlwaysUseDefaultTargetUrl(true);
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            userRepository.save(User.from(SimpleUser.from(oidcUser)));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}