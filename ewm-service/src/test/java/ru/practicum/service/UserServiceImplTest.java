package ru.practicum.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserDtoMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userServiceTest;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDtoMapper userDtoMapper;

    @Test
    @DisplayName("Test get all with ids param functionality")
    void givenUserId_whenGetAllWithIds_thenListOfUserDtosIsReturned() {
        User user1 = DataUtils.getUserAuthorPersisted();
        User user2 = DataUtils.getUserInitiatorPersisted();
        List<Long> ids = List.of(user1.getId(), user2.getId());
        Page<User> userPage = new PageImpl<>(List.of(user1, user2), Pageable.unpaged(), 2);

        BDDMockito.given(userRepository.findByIds(ids, Pageable.unpaged())).willReturn(userPage);
        BDDMockito.given(userDtoMapper.userToUserDto(any(User.class))).willReturn(DataUtils.getUserDto());

        List<UserDto> result = userServiceTest.getAll(ids, Pageable.unpaged());
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test get all without ids param functionality")
    void givenUserId_whenGetAllWithoutIds_thenListOfUserDtosIsReturned() {
        User user1 = DataUtils.getUserAuthorPersisted();
        User user2 = DataUtils.getUserInitiatorPersisted();
        Page<User> userPage = new PageImpl<>(List.of(user1, user2), Pageable.unpaged(), 2);

        BDDMockito.given(userRepository.findAll(Pageable.unpaged())).willReturn(userPage);
        BDDMockito.given(userDtoMapper.userToUserDto(any(User.class))).willReturn(DataUtils.getUserDto());

        List<UserDto> result = userServiceTest.getAll(null, Pageable.unpaged());
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test create functionality")
    void givenNewUserRequest_WhenCreate_thenUserDtoIsReturned() {

        BDDMockito.given(userDtoMapper.newUserRequestToUser(any(NewUserRequest.class)))
                .willReturn(DataUtils.getUserInitiatorTransient());
        BDDMockito.given(userDtoMapper.userToUserDto(any(User.class)))
                .willReturn(DataUtils.getUserDto());
        BDDMockito.given(userRepository.save(any(User.class))).willReturn(DataUtils.getUserInitiatorPersisted());

        UserDto result = userServiceTest.create(DataUtils.getNewUserRequest());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("User name");
    }

    @Test
    @DisplayName("Test delete functionality")
    void givenId_whenDelete_thenRepositoryDeleteMethodIsUsed() {
        userServiceTest.delete(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}