package de.sollfrank.sharedlists.model.dto;

import de.sollfrank.sharedlists.model.InviteStatus;
import de.sollfrank.sharedlists.model.ListRole;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record InviteSummary(
        UUID id,
        UUID listId,
        String listTitle,
        String inviteeEmail,
        String invitedByName,
        ListRole role,
        InviteStatus status,
        Instant createdAt
) implements Serializable {}
