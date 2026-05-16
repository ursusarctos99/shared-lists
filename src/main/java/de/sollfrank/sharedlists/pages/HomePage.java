package de.sollfrank.sharedlists.pages;

import de.sollfrank.sharedlists.SharedListsSession;
import de.sollfrank.sharedlists.SimpleUser;
import de.sollfrank.sharedlists.model.ListRole;
import de.sollfrank.sharedlists.model.dto.SharedListSummary;
import de.sollfrank.sharedlists.model.dto.SharedWithMeSummary;
import de.sollfrank.sharedlists.model.forms.InviteForm;
import de.sollfrank.sharedlists.model.forms.SharedListForm;
import de.sollfrank.sharedlists.services.SharedListService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class HomePage extends LayoutPage {

    private static final int ITEMS_PER_PAGE = 9;

    private static final String[] CARD_GRADIENTS = {
        "bg-gradient-to-br from-violet-500 to-indigo-700",
        "bg-gradient-to-br from-pink-500 to-rose-600",
        "bg-gradient-to-br from-amber-400 to-orange-600",
        "bg-gradient-to-br from-emerald-400 to-teal-600",
        "bg-gradient-to-br from-sky-400 to-blue-600",
        "bg-gradient-to-br from-fuchsia-400 to-purple-600",
        "bg-gradient-to-br from-lime-400 to-green-600",
        "bg-gradient-to-br from-red-500 to-rose-700",
        "bg-gradient-to-br from-yellow-400 to-amber-500",
    };

    @SpringBean
    private SharedListService sharedListService;

    private WebMarkupContainer listContainer;
    private DaisyPagingNavigator pager;
    private final IModel<UUID> selectedListId = new Model<>();

    public HomePage(final PageParameters parameters) {
        super(parameters);

        String displayName = SharedListsSession.get().getUser()
                .map(SimpleUser::displayName)
                .orElse("there");
        UUID ownerId = SharedListsSession.get().getUser()
                .map(u -> UUID.fromString(u.id()))
                .orElse(null);

        add(new Label("heading", new StringResourceModel("heading", this).setParameters(displayName)));

        // ── Owned lists ───────────────────────────────────────────────────
        listContainer = new WebMarkupContainer("listContainer");
        listContainer.setOutputMarkupId(true);

        DataView<SharedListSummary> listView = new DataView<>("listView",
                new SharedListDataProvider(sharedListService, ownerId)) {
            @Override
            protected void populateItem(Item<SharedListSummary> item) {
                SharedListSummary s = item.getModelObject();

                item.add(new AttributeAppender("class",
                        " " + CARD_GRADIENTS[item.getIndex() % CARD_GRADIENTS.length]));

                PageParameters params = new PageParameters();
                params.set("id", s.id().toString());
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<>(
                        "detailLink", ListDetailPage.class, params);
                link.add(new Label("title", s.title()));
                link.add(new Label("description", s.description() != null ? s.description() : ""));
                link.add(new Label("entryCount", s.entryCount()));
                item.add(link);

                UUID listId = s.id();
                item.add(new AjaxLink<Void>("deleteListLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        sharedListService.deleteList(listId);
                        target.add(listContainer, pager);
                    }
                });

                item.add(new AjaxLink<Void>("shareLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        selectedListId.setObject(listId);
                        target.appendJavaScript("document.getElementById('shareModal').showModal()");
                    }
                });
            }
        };
        listView.setItemsPerPage(ITEMS_PER_PAGE);
        listContainer.add(listView);
        add(listContainer);

        pager = new DaisyPagingNavigator("pager", listView, listContainer, ITEMS_PER_PAGE);
        add(pager);

        // ── Shared with me ────────────────────────────────────────────────
        LoadableDetachableModel<List<SharedWithMeSummary>> sharedModel =
                new LoadableDetachableModel<>() {
                    @Override
                    protected List<SharedWithMeSummary> load() {
                        return ownerId != null
                                ? sharedListService.getSharedWithMe(ownerId)
                                : List.of();
                    }
                };

        WebMarkupContainer sharedSection = new WebMarkupContainer("sharedSection") {
            @Override
            public boolean isVisible() {
                return !sharedModel.getObject().isEmpty();
            }
        };
        sharedSection.setOutputMarkupPlaceholderTag(true);

        sharedSection.add(new ListView<>("sharedView", sharedModel) {
            @Override
            protected void populateItem(ListItem<SharedWithMeSummary> item) {
                SharedWithMeSummary s = item.getModelObject();
                item.add(new AttributeAppender("class",
                        " " + CARD_GRADIENTS[item.getIndex() % CARD_GRADIENTS.length]));

                PageParameters params = new PageParameters();
                params.set("id", s.id().toString());
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<>(
                        "sharedDetailLink", ListDetailPage.class, params);
                link.add(new Label("sharedTitle", s.title()));
                link.add(new Label("sharedDescription", s.description() != null ? s.description() : ""));
                link.add(new Label("sharedEntryCount", s.entryCount()));
                link.add(new Label("sharedRole",
                        getString("role." + s.role().name().toLowerCase())));
                item.add(link);
            }
        });
        add(sharedSection);

        // ── Create list form ──────────────────────────────────────────────
        SharedListForm formModel = new SharedListForm();
        Form<SharedListForm> createForm = new Form<>("createForm",
                new CompoundPropertyModel<>(formModel));

        FeedbackPanel feedback = new FeedbackPanel("feedback") {
            @Override
            public boolean isVisible() {
                return anyMessage();
            }
        };
        feedback.setOutputMarkupPlaceholderTag(true);
        createForm.add(feedback);

        createForm.add(new TextField<String>("title").setRequired(true));
        createForm.add(new TextArea<String>("description"));
        createForm.add(new AjaxSubmitLink("submit", createForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                sharedListService.putList(formModel);
                formModel.setTitle(null);
                formModel.setDescription(null);
                target.add(createForm, listContainer, pager);
                target.appendJavaScript("document.getElementById('createListModal').close()");
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(createForm);
            }
        });
        createForm.setOutputMarkupId(true);
        add(createForm);

        // ── Share / invite form ───────────────────────────────────────────
        InviteForm inviteFormModel = new InviteForm();
        Form<InviteForm> shareForm = new Form<>("shareForm",
                new CompoundPropertyModel<>(inviteFormModel));

        FeedbackPanel shareFeedback = new FeedbackPanel("shareFeedback") {
            @Override
            public boolean isVisible() {
                return anyMessage();
            }
        };
        shareFeedback.setOutputMarkupPlaceholderTag(true);
        shareForm.add(shareFeedback);

        shareForm.add(new TextField<String>("username").setRequired(true));

        List<ListRole> roleChoices = Arrays.asList(ListRole.values());
        DropDownChoice<ListRole> roleChoice = new DropDownChoice<>("role", roleChoices,
                new IChoiceRenderer<ListRole>() {
                    @Override
                    public Object getDisplayValue(ListRole role) {
                        return HomePage.this.getString("role." + role.name().toLowerCase());
                    }

                    @Override
                    public String getIdValue(ListRole role, int index) {
                        return role.name();
                    }

                    @Override
                    public ListRole getObject(String id, IModel<? extends List<? extends ListRole>> choices) {
                        return id == null || id.isEmpty() ? null : ListRole.valueOf(id);
                    }
                });
        roleChoice.setRequired(true);
        shareForm.add(roleChoice);

        shareForm.add(new AjaxSubmitLink("shareSubmit", shareForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    sharedListService.inviteUser(selectedListId.getObject(),
                            inviteFormModel.getUsername(), inviteFormModel.getRole());
                    inviteFormModel.setUsername(null);
                    target.add(shareForm);
                    target.appendJavaScript("document.getElementById('shareModal').close()");
                } catch (IllegalArgumentException e) {
                    shareForm.error(getString("share.user.not.found"));
                    target.add(shareForm);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(shareForm);
            }
        });
        shareForm.setOutputMarkupId(true);
        add(shareForm);
    }

    private static class SharedListDataProvider implements IDataProvider<SharedListSummary>, Serializable {

        private final SharedListService service;
        private final UUID ownerId;

        SharedListDataProvider(SharedListService service, UUID ownerId) {
            this.service = service;
            this.ownerId = ownerId;
        }

        @Override
        public Iterator<SharedListSummary> iterator(long first, long count) {
            int page = (int) (first / ITEMS_PER_PAGE);
            return service.getPagedLists(ownerId, PageRequest.of(page, ITEMS_PER_PAGE, Sort.by(Sort.Direction.ASC, "title"))).iterator();
        }

        @Override
        public long size() {
            return service.getPagedLists(ownerId, PageRequest.of(0, 1)).getTotalElements();
        }

        @Override
        public IModel<SharedListSummary> model(SharedListSummary object) {
            return Model.of(object);
        }

        @Override
        public void detach() {}
    }
}
