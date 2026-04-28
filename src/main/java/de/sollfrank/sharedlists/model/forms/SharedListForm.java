package de.sollfrank.sharedlists.model.forms;

import de.sollfrank.sharedlists.model.SharedList;

import java.io.Serializable;

public class SharedListForm implements Serializable {

    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static SharedListForm of(SharedList sharedList) {
        SharedListForm sharedListForm = new SharedListForm();
        sharedListForm.setTitle(sharedList.getTitle());
        sharedListForm.setDescription(sharedListForm.getDescription());
        return sharedListForm;
    }
}
