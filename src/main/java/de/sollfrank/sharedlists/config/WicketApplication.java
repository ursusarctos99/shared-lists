package de.sollfrank.sharedlists.config;

import de.sollfrank.sharedlists.SharedListsSession;
import de.sollfrank.sharedlists.pages.HomePage;
import de.sollfrank.sharedlists.pages.ListDetailPage;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
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
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        mountPage("/lists/${id}", ListDetailPage.class);
    }
}