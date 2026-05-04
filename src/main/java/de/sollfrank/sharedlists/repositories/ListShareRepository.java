package de.sollfrank.sharedlists.repositories;

import de.sollfrank.sharedlists.model.ListShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListShareRepository extends JpaRepository<ListShare, UUID> {
}
