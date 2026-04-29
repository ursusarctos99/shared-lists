package de.sollfrank.sharedlists.services;

import de.sollfrank.sharedlists.model.ListEntry;
import de.sollfrank.sharedlists.model.forms.ListEntryForm;
import de.sollfrank.sharedlists.repositories.ListEntryRepository;
import de.sollfrank.sharedlists.repositories.SharedListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListEntryService {

    private final ListEntryRepository listEntryRepository;
    private final SharedListRepository sharedListRepository;

    public ListEntryService(ListEntryRepository listEntryRepository,
                             SharedListRepository sharedListRepository) {
        this.listEntryRepository = listEntryRepository;
        this.sharedListRepository = sharedListRepository;
    }

    public List<ListEntry> getEntriesForList(UUID listId) {
        return listEntryRepository.findByListIdOrderByCreatedAtAsc(listId);
    }

    @Transactional
    public ListEntry addEntry(UUID listId, ListEntryForm form) {
        ListEntry entry = new ListEntry();
        entry.setList(sharedListRepository.getReferenceById(listId));
        entry.setTitle(form.getTitle());
        String url = form.getUrl();
        entry.setUrl(url != null && !url.isBlank() ? url : null);
        return listEntryRepository.save(entry);
    }

    @Transactional
    public void setDone(UUID entryId, boolean done) {
        listEntryRepository.findById(entryId).ifPresent(e -> e.setDone(done));
    }

    @Transactional
    public void deleteEntry(UUID entryId) {
        listEntryRepository.deleteById(entryId);
    }
}