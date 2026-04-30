package de.sollfrank.sharedlists.services;

import de.sollfrank.sharedlists.model.ListEntry;
import de.sollfrank.sharedlists.model.SharedList;
import de.sollfrank.sharedlists.model.User;
import de.sollfrank.sharedlists.model.dto.SharedListSummary;
import de.sollfrank.sharedlists.model.forms.SharedListForm;
import de.sollfrank.sharedlists.repositories.SharedListRepository;
import de.sollfrank.sharedlists.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import({TestcontainersConfiguration.class, SharedListServiceTest.AuditConfig.class})
class SharedListServiceTest {

    static final UUID TEST_USER_ID = UUID.randomUUID();

    @TestConfiguration
    static class AuditConfig {
        @Bean
        AuditorAware<User> auditorAware(UserRepository userRepository) {
            return () -> userRepository.findById(TEST_USER_ID);
        }
    }

    @Autowired SharedListService service;
    @Autowired SharedListRepository repository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setDisplayName("Test User");
        userRepository.save(testUser);
    }

    @Test
    void putList_persistsAndReturnId() {
        SharedListForm form = form("Groceries", "Weekly shopping");

        UUID id = service.putList(form);

        assertThat(id).isNotNull();
        SharedList saved = repository.findById(id).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("Groceries");
        assertThat(saved.getDescription()).isEqualTo("Weekly shopping");
        assertThat(saved.getOwner().getId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    void getList_returnsEntity() {
        UUID id = service.putList(form("Movies", null));

        SharedList found = service.getList(id);

        assertThat(found.getId()).isEqualTo(id);
        assertThat(found.getTitle()).isEqualTo("Movies");
        assertThat(found.getDescription()).isNull();
    }

    @Test
    void getList_throwsWhenNotFound() {
        assertThatThrownBy(() -> service.getList(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getPagedLists_returnsCorrectEntryCount() {
        UUID id = service.putList(form("Books", null));

        SharedList list = repository.findById(id).orElseThrow();
        ListEntry e1 = entry(list, "Dune");
        ListEntry e2 = entry(list, "Foundation");
        list.getEntries().add(e1);
        list.getEntries().add(e2);
        repository.save(list);

        service.putList(form("Empty list", null));

        Page<SharedListSummary> page = service.getPagedLists(TEST_USER_ID, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        SharedListSummary books = page.getContent().stream()
                .filter(s -> s.title().equals("Books"))
                .findFirst().orElseThrow();
        assertThat(books.entryCount()).isEqualTo(2);

        SharedListSummary empty = page.getContent().stream()
                .filter(s -> s.title().equals("Empty list"))
                .findFirst().orElseThrow();
        assertThat(empty.entryCount()).isEqualTo(0);
    }

    @Test
    void getPagedLists_doesNotReturnOtherUsersLists() {
        service.putList(form("My List", null));

        UUID otherId = UUID.randomUUID();
        Page<SharedListSummary> page = service.getPagedLists(otherId, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(0);
    }

    @Test
    void deleteList_removesEntity() {
        UUID id = service.putList(form("Temp", null));

        service.deleteList(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    private static SharedListForm form(String title, String description) {
        SharedListForm f = new SharedListForm();
        f.setTitle(title);
        f.setDescription(description);
        return f;
    }

    private static ListEntry entry(SharedList list, String title) {
        ListEntry e = new ListEntry();
        e.setList(list);
        e.setTitle(title);
        return e;
    }
}