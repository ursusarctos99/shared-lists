package de.sollfrank.sharedlists.repositories;

import de.sollfrank.sharedlists.model.ListInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListInviteRepository extends JpaRepository<ListInvite, UUID> {
}