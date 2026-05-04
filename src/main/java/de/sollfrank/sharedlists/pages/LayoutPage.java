package de.sollfrank.sharedlists.pages;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forUrl("https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"));
    }

    private void addComponents() {
        Form<Void> searchForm = new Form<>("searchForm");
        searchForm.add(new TextField<>("searchInput", Model.of("")));
        add(searchForm);

        add(new BookmarkablePageLink<>("messagesLink", MessagesPage.class));
    }
}