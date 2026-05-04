package de.sollfrank.sharedlists.model.forms;

import de.sollfrank.sharedlists.model.ListRole;
import java.io.Serializable;

public class InviteForm implements Serializable {

    private String username;
    private ListRole role = ListRole.EDITOR;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public ListRole getRole() { return role; }
    public void setRole(ListRole role) { this.role = role; }
}
