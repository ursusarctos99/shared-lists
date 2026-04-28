package de.sollfrank.sharedlists.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SettingsPage extends LayoutPage {

    public SettingsPage(PageParameters parameters) {
        super(parameters);
        add(new Label("heading", new StringResourceModel("heading", this)));
    }
}