package de.sollfrank.sharedlists.model.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record SharedListSummary(
        UUID id,
        String title,
        String description,
        Instant createdAt,
        long entryCount
) implements Serializable {}