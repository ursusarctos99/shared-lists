package de.sollfrank.sharedlists.repositories;

import de.sollfrank.sharedlists.model.ListInvite;
import de.sollfrank.sharedlists.model.dto.InviteSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ListInviteRepository extends JpaRepository<ListInvite, UUID> {

    @Query("""
            SELECT new de.sollfrank.sharedlists.model.dto.InviteSummary(
                i.id, i.list.id, i.list.title, i.inviteeEmail, i.invitedByName,
                i.role, i.status, i.createdAt)
            FROM ListInvite i
            WHERE i.invitedBy.id = :userId
            ORDER BY i.createdAt DESC
            """)
    List<InviteSummary> findSentSummariesByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT new de.sollfrank.sharedlists.model.dto.InviteSummary(
                i.id, i.list.id, i.list.title, i.inviteeEmail, i.invitedByName,
                i.role, i.status, i.createdAt)
            FROM ListInvite i
            WHERE i.inviteeEmail = :email
            ORDER BY i.createdAt DESC
            """)
    List<InviteSummary> findReceivedSummariesByEmail(@Param("email") String email);
}
