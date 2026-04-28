package de.sollfrank.sharedlists.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LayoutPage extends WebPage {

    public LayoutPage() {
        addComponents();
    }

    public LayoutPage(PageParameters parameters) {
        super(parameters);
        addComponents();
    }

    private void addComponents() {
        Form<Void> searchForm = new Form<>("searchForm");
        searchForm.add(new TextField<>("searchInput", Model.of("")));
        add(searchForm);

        add(new BookmarkablePageLink<>("settingsLink", SettingsPage.class));
    }
}