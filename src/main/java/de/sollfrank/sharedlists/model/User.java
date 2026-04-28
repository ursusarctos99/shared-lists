package de.sollfrank.sharedlists.model;

import de.sollfrank.sharedlists.SimpleUser;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Instant getCreatedAt() { return createdAt; }

    public static User from(SimpleUser simpleUser) {
        User user = new User();
        user.setId(UUID.fromString(simpleUser.id()));
        user.setEmail(simpleUser.email());
        user.setUsername(simpleUser.username());
        user.setDisplayName(simpleUser.displayName());
        return user;
    }
}