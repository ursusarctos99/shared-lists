package de.sollfrank.sharedlists.repositories;

import de.sollfrank.sharedlists.model.SharedList;
import de.sollfrank.sharedlists.model.dto.SharedListSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SharedListRepository extends JpaRepository<SharedList, UUID> {

    @Query(
        value = """
            SELECT new de.sollfrank.sharedlists.model.dto.SharedListSummary(
                sl.id, sl.title, sl.description, sl.createdAt, COUNT(e)
            )
            FROM SharedList sl LEFT JOIN sl.entries e
            WHERE sl.owner.id = :ownerId
            GROUP BY sl.id, sl.title, sl.description, sl.createdAt
            """,
        countQuery = "SELECT COUNT(sl) FROM SharedList sl WHERE sl.owner.id = :ownerId"
    )
    Page<SharedListSummary> findSummariesByOwner(@Param("ownerId") UUID ownerId, Pageable pageable);
}