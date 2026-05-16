package de.sollfrank.sharedlists.pages;

import de.sollfrank.sharedlists.SharedListsSession;
import de.sollfrank.sharedlists.SimpleUser;
import de.sollfrank.sharedlists.model.InviteStatus;
import de.sollfrank.sharedlists.model.dto.InviteSummary;
import de.sollfrank.sharedlists.services.SharedListService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.UUID;

public class MessagesPage extends LayoutPage {

    @SpringBean
    private SharedListService sharedListService;

    public MessagesPage(PageParameters parameters) {
        super(parameters);

        add(new BookmarkablePageLink<>("backLink", HomePage.class));

        SimpleUser currentUser = SharedListsSession.get().getUser().orElseThrow();
        UUID currentUserId = UUID.fromString(currentUser.id());

        // ── Received invites ──────────────────────────────────────────────
        WebMarkupContainer receivedContainer = new WebMarkupContainer("receivedContainer");
        receivedContainer.setOutputMarkupId(true);

        LoadableDetachableModel<List<InviteSummary>> receivedModel =
                new LoadableDetachableModel<>() {
                    @Override
                    protected List<InviteSummary> load() {
                        return sharedListService.getReceivedInvites(currentUserId);
                    }
                };

        receivedContainer.add(new WebMarkupContainer("noReceived") {
            @Override
            public boolean isVisible() {
                return receivedModel.getObject().isEmpty();
            }
        }.setOutputMarkupPlaceholderTag(true));

        receivedContainer.add(new ListView<>("receivedList", receivedModel) {
            @Override
            protected void populateItem(ListItem<InviteSummary> item) {
                InviteSummary invite = item.getModelObject();
                item.add(new Label("receivedListTitle", invite.listTitle()));
                item.add(new Label("receivedInvitedBy", invite.invitedByDisplayName()));
                item.add(new Label("receivedRole", invite.role().name().toLowerCase()));

                boolean isPending = invite.status() == InviteStatus.PENDING;
                WebMarkupContainer actions = new WebMarkupContainer("receivedActions");
                actions.setVisible(isPending);
                actions.setOutputMarkupPlaceholderTag(true);
                actions.add(new AjaxLink<Void>("acceptLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        sharedListService.acceptInvite(invite.id());
                        receivedModel.detach();
                        target.add(receivedContainer);
                    }
                });
                actions.add(new AjaxLink<Void>("rejectLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        sharedListService.rejectInvite(invite.id());
                        receivedModel.detach();
                        target.add(receivedContainer);
                    }
                });
                item.add(actions);

                Label statusBadge = new Label("receivedStatus",
                        getString("status." + invite.status().name().toLowerCase()));
                statusBadge.add(AttributeAppender.replace("class", statusBadgeClass(invite.status())));
                item.add(statusBadge);
            }
        });

        add(receivedContainer);

        // ── Sent invites ──────────────────────────────────────────────────
        WebMarkupContainer sentContainer = new WebMarkupContainer("sentContainer");
        sentContainer.setOutputMarkupId(true);

        LoadableDetachableModel<List<InviteSummary>> sentModel =
                new LoadableDetachableModel<>() {
                    @Override
                    protected List<InviteSummary> load() {
                        return sharedListService.getSentInvites(currentUserId);
                    }
                };

        sentContainer.add(new WebMarkupContainer("noSent") {
            @Override
            public boolean isVisible() {
                return sentModel.getObject().isEmpty();
            }
        }.setOutputMarkupPlaceholderTag(true));

        sentContainer.add(new ListView<>("sentList", sentModel) {
            @Override
            protected void populateItem(ListItem<InviteSummary> item) {
                InviteSummary invite = item.getModelObject();
                item.add(new Label("sentListTitle", invite.listTitle()));
                item.add(new Label("sentInviteeEmail", invite.inviteeDisplayName()));
                item.add(new Label("sentRole", invite.role().name().toLowerCase()));
                Label statusBadge = new Label("sentStatus",
                        getString("status." + invite.status().name().toLowerCase()));
                statusBadge.add(AttributeAppender.replace("class", statusBadgeClass(invite.status())));
                item.add(statusBadge);
            }
        });

        add(sentContainer);
    }

    private static String statusBadgeClass(InviteStatus status) {
        String color = switch (status) {
            case PENDING -> "badge-warning";
            case ACCEPTED -> "badge-success";
            case REJECTED -> "badge-error";
            case EXPIRED -> "badge-neutral";
        };
        return "badge shrink-0 " + color;
    }
}
