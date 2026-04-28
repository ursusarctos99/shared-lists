package de.sollfrank.sharedlists.services;

import de.sollfrank.sharedlists.model.SharedList;
import de.sollfrank.sharedlists.model.dto.SharedListSummary;
import de.sollfrank.sharedlists.model.forms.SharedListForm;
import de.sollfrank.sharedlists.repositories.SharedListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SharedListService {

    private final SharedListRepository sharedListRepository;

    public SharedListService(SharedListRepository sharedListRepository) {
        this.sharedListRepository = sharedListRepository;
    }

    @Transactional
    public UUID putList(SharedListForm sharedListForm) {
        return sharedListRepository.save(SharedList.of(sharedListForm)).getId();
    }

    @Transactional
    public UUID deleteList(UUID sharedListId) {
        sharedListRepository.deleteById(sharedListId);
        return sharedListId;
    }

    public SharedList getList(UUID id) {
        return sharedListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SharedList not found: " + id));
    }

    public Page<SharedListSummary> getPagedLists(UUID ownerId, Pageable pageable) {
        return sharedListRepository.findSummariesByOwner(ownerId, pageable);
    }
}