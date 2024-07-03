package ru.practicum.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.model.User;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test find users by ids functionality")
    void givenTwoUserIds_whenFindByIds_ThenTwoUsersAreReturned() {
        User user1 = DataUtils.getUserInitiatorTransient();
        User user2 = DataUtils.getUserAuthorTransient();
        userRepository.save(user1);
        userRepository.save(user2);

        Page<User> userPage = userRepository.findByIds(List.of(user1.getId(), user2.getId()), Pageable.unpaged());

        assertFalse(userPage.isEmpty());
        assertThat(userPage.getContent().size()).isEqualTo(2);
    }
}