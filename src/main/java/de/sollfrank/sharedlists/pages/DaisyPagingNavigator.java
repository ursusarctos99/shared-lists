package de.sollfrank.sharedlists.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

public class DaisyPagingNavigator extends Panel {

    private final DataView<?> dataView;
    private final WebMarkupContainer refreshTarget;
    private final int itemsPerPage;

    public DaisyPagingNavigator(String id, DataView<?> dataView,
                                 WebMarkupContainer refreshTarget, int itemsPerPage) {
        super(id);
        this.dataView = dataView;
        this.refreshTarget = refreshTarget;
        this.itemsPerPage = itemsPerPage;
        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new Label("pageInfo", new StringResourceModel("page.info", this)
                .setParameters(
                        (IModel<Long>) () -> dataView.getCurrentPage() + 1,
                        (IModel<Long>) () -> dataView.getPageCount()
                )));

        add(new Label("perPage", new StringResourceModel("per.page", this)
                .setParameters(itemsPerPage)));

        add(navButton("first",
                () -> 0L,
                () -> dataView.getCurrentPage() > 0));
        add(navButton("prev",
                () -> Math.max(0L, dataView.getCurrentPage() - 1),
                () -> dataView.getCurrentPage() > 0));
        add(navButton("next",
                () -> Math.min(dataView.getPageCount() - 1, dataView.getCurrentPage() + 1),
                () -> dataView.getCurrentPage() < dataView.getPageCount() - 1));
        add(navButton("last",
                () -> dataView.getPageCount() - 1,
                () -> dataView.getCurrentPage() < dataView.getPageCount() - 1));
    }

    private AjaxLink<Void> navButton(String id, IModel<Long> targetPage, IModel<Boolean> enabled) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                dataView.setCurrentPage(targetPage.getObject());
                target.add(refreshTarget, DaisyPagingNavigator.this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(enabled.getObject());
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.setName("a");
                if (!isEnabledInHierarchy()) {
                    tag.append("class", "btn-disabled", " ");
                    tag.put("aria-disabled", "true");
                }
            }
        };
    }
}