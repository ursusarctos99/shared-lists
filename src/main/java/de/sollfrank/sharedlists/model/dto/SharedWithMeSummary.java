package de.sollfrank.sharedlists.model.dto;

import de.sollfrank.sharedlists.model.ListRole;

import java.io.Serializable;
import java.util.UUID;

public record SharedWithMeSummary(
        UUID id,
        String title,
        String description,
        long entryCount,
        ListRole role
) implements Serializable {}
