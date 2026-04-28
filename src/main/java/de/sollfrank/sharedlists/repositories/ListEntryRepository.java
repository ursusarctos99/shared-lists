package de.sollfrank.sharedlists.repositories;

import de.sollfrank.sharedlists.model.ListEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListEntryRepository extends JpaRepository<ListEntry, UUID> {

    List<ListEntry> findByListIdOrderByCreatedAtAsc(UUID listId);
}