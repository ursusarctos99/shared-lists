package de.sollfrank.sharedlists.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "list_share",
    uniqueConstraints = @UniqueConstraint(columnNames = {"list_id", "user_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class ListShare {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private SharedList list;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListRole role;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }

    public SharedList getList() { return list; }
    public void setList(SharedList list) { this.list = list; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ListRole getRole() { return role; }
    public void setRole(ListRole role) { this.role = role; }

    public Instant getCreatedAt() { return createdAt; }
}