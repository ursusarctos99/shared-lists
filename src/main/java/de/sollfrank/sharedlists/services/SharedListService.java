package de.sollfrank.sharedlists.services;

import de.sollfrank.sharedlists.model.*;
import de.sollfrank.sharedlists.model.dto.InviteSummary;
import de.sollfrank.sharedlists.model.dto.SharedListSummary;
import de.sollfrank.sharedlists.model.dto.SharedWithMeSummary;
import de.sollfrank.sharedlists.model.forms.SharedListForm;
import de.sollfrank.sharedlists.repositories.ListInviteRepository;
import de.sollfrank.sharedlists.repositories.ListShareRepository;
import de.sollfrank.sharedlists.repositories.SharedListRepository;
import de.sollfrank.sharedlists.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SharedListService {

    private final SharedListRepository sharedListRepository;
    private final UserRepository userRepository;
    private final ListInviteRepository listInviteRepository;
    private final ListShareRepository listShareRepository;

    public SharedListService(SharedListRepository sharedListRepository,
                             UserRepository userRepository,
                             ListInviteRepository listInviteRepository,
                             ListShareRepository listShareRepository) {
        this.sharedListRepository = sharedListRepository;
        this.userRepository = userRepository;
        this.listInviteRepository = listInviteRepository;
        this.listShareRepository = listShareRepository;
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
    public void inviteUser(UUID listId, String username, ListRole role) {
        User invitee = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        SharedList list = sharedListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("SharedList not found: " + listId));
        ListInvite invite = new ListInvite();
        invite.setList(list);
        invite.setInvitee(invitee);
        invite.setRole(role);
        invite.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        listInviteRepository.save(invite);
    }

    public List<SharedWithMeSummary> getSharedWithMe(UUID userId) {
        return sharedListRepository.findSharedWithUser(userId);
    }

    public List<InviteSummary> getSentInvites(UUID userId) {
        return listInviteRepository.findSentSummariesByUserId(userId);
    }

    public boolean canEdit(UUID listId, UUID userId) {
        if (userId == null) return false;
        if (sharedListRepository.existsByIdAndOwnerId(listId, userId)) return true;
        return listShareRepository.findByListIdAndUserId(listId, userId)
                .map(share -> share.getRole() == ListRole.EDITOR)
                .orElse(false);
    }

    public List<InviteSummary> getReceivedInvites(UUID userId) {
        return listInviteRepository.findReceivedSummariesByUserId(userId);
    }

    @Transactional
    public void acceptInvite(UUID inviteId) {
        ListInvite invite = listInviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found: " + inviteId));
        invite.setStatus(InviteStatus.ACCEPTED);
        invite.setAcceptedAt(Instant.now());
        ListShare share = new ListShare();
        share.setList(invite.getList());
        share.setUser(invite.getInvitee());
        share.setRole(invite.getRole());
        listShareRepository.save(share);
    }

    @Transactional
    public void rejectInvite(UUID inviteId) {
        ListInvite invite = listInviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found: " + inviteId));
        invite.setStatus(InviteStatus.REJECTED);
    }
}