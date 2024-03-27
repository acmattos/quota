package br.com.acmattos.quota.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.acmattos.quota.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@SpringBootTest
class UserServiceImplTest {
    private static final String USER_ID = "170cc0f6-5936-4a95-8fa8-a9c5611d6472";
    @MockBean
    private UserPersistenceService persistenceService;
    private UserService service;

    @BeforeEach
    public void setUp() {
        service = new UserServiceImpl(persistenceService);
    }

    @Test
    void givenNewUserWhenCreateUserIsCalledThenAnNonEmptyOptionalIsReturned(){
        // Given
        User user = new User();
        when(persistenceService.getUserByFirstNameAndLastName(any(), any()))
            .thenReturn(Optional.empty());
        when(persistenceService.createUser(any()))
            .thenReturn(user);
        // When
        Optional<User> created = service.createUser(user);
        // Then
        assertEquals(user.getId(),created.get().getId());
        verify(persistenceService).createUser(user);
    }

    @Test
    void givenExistentUserWhenCreateUserIsCalledThenAnEmptyOptionalIsReturned(){
        // Given
        User user = new User();
        when(persistenceService.getUserByFirstNameAndLastName(any(), any()))
            .thenReturn(Optional.of(user));
        // When
        Optional<User> created = service.createUser(user);
        // Then
        assertThat(created).isEmpty();
        verify(persistenceService, never()).createUser(user);
    }

    @Test
    void givenNewUserWhenGetUserByIdIsCalledThenAnNonEmptyOptionalIsReturned(){
        // Given
        User user = new User();
        when(persistenceService.getUserById(any()))
            .thenReturn(Optional.of(user));
        // When
        Optional<User> created = service.getUserById(user.getId());
        // Then
        verify(persistenceService).getUserById(user.getId());
    }

    @Test
    void givenExistentUserWhenGetUserByIdIsCalledThenAnEmptyOptionalIsReturned(){
        // Given
        User user = new User();
        when(persistenceService.getUserById(any()))
            .thenReturn(Optional.empty());
        // When
        Optional<User> created = service.getUserById(user.getId());
        // Then
        assertThat(created).isEmpty();
        verify(persistenceService, times(1)).getUserById(user.getId());
    }

    @Test
    void givenAnOldUserWhenUpdateUserIsCalledThenAnNonEmptyOptionalIsReturned(){
        // Given
        User user = new User().setId(USER_ID);
        when(persistenceService.getUserById(any()))
            .thenReturn(Optional.of(user));
        when(persistenceService.updateUser(any()))
            .thenReturn(user);
        // When
        Optional<User> updated = service.updateUser(user.getId(), user);
        // Then
        assertEquals(user.getId(),updated.get().getId());
        verify(persistenceService).getUserById(user.getId());
        verify(persistenceService).updateUser(user);
    }

    @Test
    void givenNonExistentUserIdWhenUpdateUserIsCalledThenAnEmptyOptionalIsReturned(){
        // Given
        User user = new User().setId(USER_ID);
        when(persistenceService.getUserById(any()))
            .thenReturn(Optional.empty());
        // When
        Optional<User> updated = service.updateUser(user.getId(), user);
        // Then
        assertThat(updated).isEmpty();
        verify(persistenceService).getUserById(user.getId());
        verify(persistenceService, never()).updateUser(user);
    }

    @Test
    void givenAnOldUserWhenDeleteUserIsCalledThenAnNonEmptyOptionalIsReturned(){
        // Given
        User user = new User().setId(USER_ID);
        when(persistenceService.getUserById(any()))
            .thenReturn(Optional.of(user));
        when(persistenceService.deleteUser(any()))
            .thenReturn(user);
        // When
        Optional<User> updated = service.deleteUserById(user.getId());
        // Then
        assertEquals(user.getId(),updated.get().getId());
        verify(persistenceService).getUserById(user.getId());
        verify(persistenceService).deleteUser(user);
    }

    @Test
    void givenNonExistentUserIdWhenDeleteUserIsCalledThenAnEmptyOptionalIsReturned(){
        // Given
        User user = new User().setId(USER_ID);
        when(persistenceService.getUserById(any()))
            .thenReturn(Optional.empty());
        // When
        Optional<User> updated = service.deleteUserById(user.getId());
        // Then
        assertThat(updated).isEmpty();
        verify(persistenceService).getUserById(user.getId());
        verify(persistenceService, never()).deleteUser(user);
    }
}