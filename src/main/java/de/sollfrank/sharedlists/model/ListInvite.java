package de.sollfrank.sharedlists.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "list_invite")
@EntityListeners(AuditingEntityListener.class)
public class ListInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private SharedList list;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by", nullable = false, updatable = false)
    private User invitedBy;

    @Column(name = "invited_by_name", nullable = false)
    private String invitedByName;

    @Column(name = "invitee_email", nullable = false)
    private String inviteeEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status = InviteStatus.PENDING;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    public UUID getId() { return id; }

    public SharedList getList() { return list; }
    public void setList(SharedList list) { this.list = list; }

    public User getInvitedBy() { return invitedBy; }
    public void setInvitedBy(User invitedBy) { this.invitedBy = invitedBy; }

    public String getInvitedByName() { return invitedByName; }
    public void setInvitedByName(String invitedByName) { this.invitedByName = invitedByName; }

    public String getInviteeEmail() { return inviteeEmail; }
    public void setInviteeEmail(String inviteeEmail) { this.inviteeEmail = inviteeEmail; }

    public ListRole getRole() { return role; }
    public void setRole(ListRole role) { this.role = role; }

    public InviteStatus getStatus() { return status; }
    public void setStatus(InviteStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }
}