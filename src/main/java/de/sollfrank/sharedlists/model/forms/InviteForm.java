package de.sollfrank.sharedlists.model.forms;

import java.io.Serializable;

public class InviteForm implements Serializable {

    private String username;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}