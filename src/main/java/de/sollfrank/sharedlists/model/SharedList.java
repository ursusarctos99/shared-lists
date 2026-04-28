package de.sollfrank.sharedlists.model;

import de.sollfrank.sharedlists.model.forms.SharedListForm;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shared_list")
@EntityListeners(AuditingEntityListener.class)
public class SharedList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    @Column(nullable = false)
    private String title;

    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListEntry> entries = new ArrayList<>();

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ListShare> shares = new ArrayList<>();

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ListInvite> invites = new ArrayList<>();

    public UUID getId() { return id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }

    public List<ListEntry> getEntries() { return entries; }
    public List<ListShare> getShares() { return shares; }
    public List<ListInvite> getInvites() { return invites; }

    public static SharedList of(SharedListForm sharedListForm) {
        SharedList sharedList = new SharedList();
        sharedList.setTitle(sharedListForm.getTitle());
        sharedList.setDescription(sharedListForm.getDescription());
        return sharedList;
    }
}