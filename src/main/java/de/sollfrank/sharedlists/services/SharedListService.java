package de.sollfrank.sharedlists.services;

import de.sollfrank.sharedlists.model.ListInvite;
import de.sollfrank.sharedlists.model.ListRole;
import de.sollfrank.sharedlists.model.SharedList;
import de.sollfrank.sharedlists.model.User;
import de.sollfrank.sharedlists.model.dto.SharedListSummary;
import de.sollfrank.sharedlists.model.forms.SharedListForm;
import de.sollfrank.sharedlists.repositories.ListInviteRepository;
import de.sollfrank.sharedlists.repositories.SharedListRepository;
import de.sollfrank.sharedlists.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SharedListService {

    private final SharedListRepository sharedListRepository;
    private final UserRepository userRepository;
    private final ListInviteRepository listInviteRepository;

    public SharedListService(SharedListRepository sharedListRepository,
                             UserRepository userRepository,
                             ListInviteRepository listInviteRepository) {
        this.sharedListRepository = sharedListRepository;
        this.userRepository = userRepository;
        this.listInviteRepository = listInviteRepository;
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

    @Transactional
    public void inviteUser(UUID listId, String username, String invitedByName) {
        User invitee = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        SharedList list = sharedListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("SharedList not found: " + listId));
        ListInvite invite = new ListInvite();
        invite.setList(list);
        invite.setInvitedByName(invitedByName);
        invite.setInviteeEmail(invitee.getEmail());
        invite.setRole(ListRole.EDITOR);
        invite.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        listInviteRepository.save(invite);
    }
}