package de.sollfrank.sharedlists.config;

import de.sollfrank.sharedlists.SharedListsSession;
import de.sollfrank.sharedlists.pages.HomePage;
import de.sollfrank.sharedlists.pages.ListDetailPage;
import de.sollfrank.sharedlists.pages.MessagesPage;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.csp.CSPDirective;
import org.apache.wicket.csp.CSPDirectiveSrcValue;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Component;

@Component
public class WicketApplication extends WebApplication {

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new SharedListsSession(request);
    }

    @Override
    public void init() {
        super.init();
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getCspSettings().blocking().disabled();
                //.add(CSPDirective.STYLE_SRC, "'unsafe-inline'")
//                .add(CSPDirective.STYLE_SRC, CSPDirectiveSrcValue.SELF)
//                .add(CSPDirective.SCRIPT_SRC, "'unsafe-inline'")
//                .add(CSPDirective.SCRIPT_SRC, "'unsafe-eval'")
//                .add(CSPDirective.STYLE_SRC, "https://cdn.jsdelivr.net/npm/daisyui@5")
//                .add(CSPDirective.STYLE_SRC, "https://cdn.jsdelivr.net/npm/daisyui@5/themes.css")
//                .add(CSPDirective.SCRIPT_SRC, "https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4")
//                .add(CSPDirective.STYLE_SRC, "https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4");
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        mountPage("/lists/${id}", ListDetailPage.class);
        mountPage("/messages", MessagesPage.class);
    }
}
