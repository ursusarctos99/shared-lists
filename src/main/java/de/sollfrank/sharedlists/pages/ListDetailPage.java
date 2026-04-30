package de.sollfrank.sharedlists.pages;

import de.sollfrank.sharedlists.model.ListEntry;
import de.sollfrank.sharedlists.model.SharedList;
import de.sollfrank.sharedlists.model.forms.ListEntryForm;
import de.sollfrank.sharedlists.services.ListEntryService;
import de.sollfrank.sharedlists.services.SharedListService;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.UUID;

public class ListDetailPage extends LayoutPage {

    @SpringBean
    private SharedListService sharedListService;

    @SpringBean
    private ListEntryService listEntryService;

    public ListDetailPage(PageParameters parameters) {
        super(parameters);

        UUID listId;
        try {
            listId = UUID.fromString(parameters.get("id").toString());
        } catch (Exception e) {
            throw new RestartResponseException(HomePage.class);
        }

        SharedList list = sharedListService.getList(listId);

        add(new BookmarkablePageLink<>("backLink", HomePage.class));
        add(new Label("listTitle", list.getTitle()));
        add(new Label("listDescription", list.getDescription() != null ? list.getDescription() : ""));

        // Entries
        IModel<List<ListEntry>> entriesModel = new LoadableDetachableModel<>() {
            @Override
            protected List<ListEntry> load() {
                return listEntryService.getEntriesForList(listId);
            }
        };

        WebMarkupContainer entriesContainer = new WebMarkupContainer("entriesContainer");
        entriesContainer.setOutputMarkupId(true);

        ListView<ListEntry> entryList = new ListView<>("entryList", entriesModel) {
            @Override
            protected void populateItem(ListItem<ListEntry> item) {
                ListEntry entry = item.getModelObject();
                UUID entryId = entry.getId();

                CheckBox doneCheck = new CheckBox("done", Model.of(entry.isDone()));
                doneCheck.add(new AjaxFormComponentUpdatingBehavior("change") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        listEntryService.setDone(entryId, doneCheck.getModelObject());
                        entriesModel.detach();
                        target.add(entriesContainer);
                    }
                });
                item.add(doneCheck);

                Label entryTitle = new Label("entryTitle", entry.getTitle());
                if (entry.isDone()) {
                    entryTitle.add(new org.apache.wicket.behavior.AttributeAppender(
                            "class", " line-through opacity-40"));
                }
                item.add(entryTitle);

                ExternalLink urlLink = new ExternalLink("url",
                        entry.getUrl() != null ? entry.getUrl() : "");
                urlLink.setVisible(entry.getUrl() != null && !entry.getUrl().isBlank());
                item.add(urlLink);

                item.add(new AjaxLink<Void>("deleteEntryLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        listEntryService.deleteEntry(entryId);
                        entriesModel.detach();
                        target.add(entriesContainer);
                    }
                });
            }
        };
        entryList.setReuseItems(false);
        entriesContainer.add(entryList);

        Form<Void> entriesForm = new Form<>("entriesForm");
        entriesForm.add(entriesContainer);
        add(entriesForm);

        // Add-entry modal form
        ListEntryForm formModel = new ListEntryForm();
        Form<ListEntryForm> addEntryForm = new Form<>("addEntryForm",
                new CompoundPropertyModel<>(formModel));

        FeedbackPanel feedback = new FeedbackPanel("feedback") {
            @Override
            public boolean isVisible() {
                return anyMessage();
            }
        };
        feedback.setOutputMarkupPlaceholderTag(true);
        addEntryForm.add(feedback);

        addEntryForm.add(new TextField<String>("title").setRequired(true));
        addEntryForm.add(new TextField<String>("url"));

        addEntryForm.add(new AjaxSubmitLink("submit", addEntryForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                listEntryService.addEntry(listId, formModel);
                formModel.setTitle(null);
                formModel.setUrl(null);
                entriesModel.detach();
                target.add(entriesContainer);
                target.appendJavaScript("document.getElementById('addEntryModal').close()");
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(addEntryForm);
            }
        });
        addEntryForm.setOutputMarkupId(true);
        add(addEntryForm);
    }
}