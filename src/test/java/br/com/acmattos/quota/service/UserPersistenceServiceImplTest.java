package br.com.acmattos.quota.service;

import br.com.acmattos.quota.entity.User;
import br.com.acmattos.quota.repository.UserRepositoryDatabaseStrategy;
import br.com.acmattos.quota.repository.UserRepositoryElasticSearchStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserPersistenceServiceImplTest {
    @MockBean
    private Clock clock;
    @MockBean
    private UserRepositoryElasticSearchStrategy esStrategy;
    @MockBean
    private UserRepositoryDatabaseStrategy dbStrategy;
    private UserPersistenceServiceImpl service;
    public static Instant CURRENT_TIME = Instant.parse("2024-03-24T16:59:59.999Z");

    @BeforeEach
    public void setUp() {
        service = new UserPersistenceServiceImpl(clock, esStrategy, dbStrategy);
    }

    @Test
    void givenBeginningNightWhenMustUseDbRepositoryIsCalledThenTheResultIsFalse(){
        // Given
        Instant instant = Instant.parse("2024-03-24T17:01:00.000Z");
        when(clock.instant()).thenReturn(instant);
        // When
        boolean result = service.mustUseDbRepository();
        // Then
        assertFalse(result);
    }

    @Test
    void givenEndingNightWhenMustUseDbRepositoryIsCalledThenTheResultIsFalse(){
        // Given
        Instant instant = Instant.parse("2024-03-24T08:59:59.999Z");
        when(clock.instant()).thenReturn(instant);
        // When
        boolean result = service.mustUseDbRepository();
        // Then
        assertFalse(result);
    }

    @Test
    void givenBeginningDayWhenMustUseDbRepositoryIsCalledThenTheResultIsTrue(){
        // Given
        Instant instant = Instant.parse("2024-03-24T09:00:00.000Z");
        when(clock.instant()).thenReturn(instant);
        // When
        boolean result = service.mustUseDbRepository();
        // Then
        assertTrue(result);
    }

    @Test
    void givenEndingDayWhenMustUseDbRepositoryIsCalledThenTheResultIsTrue(){
        // Given
        Instant instant = Instant.parse("2024-03-24T17:00:59.999Z");
        when(clock.instant()).thenReturn(instant);
        // When
        boolean result = service.mustUseDbRepository();
        // Then
        assertTrue(result);
    }

    @Test
    void givenEndingDayWhenCreateUserIsCalledThenDbRepoIsCalled(){
        // Given
        Instant instant = Instant.parse("2024-03-24T17:00:59.999Z");
        when(clock.instant()).thenReturn(instant);
        User user = new User();
        // When
        service.createUser(user);
        // Then
        verify(esStrategy, never()).createUser(user);
        verify(dbStrategy).createUser(user);
    }

    @Test
    void givenBeginningNightWhenGetUserByFirstNameAndLastNameThenEsRepoIsCalled(){
        // Given
        Instant instant = Instant.parse("2024-03-24T17:01:00.000Z");
        when(clock.instant()).thenReturn(instant);
        User user = new User().setFirstName("John").setLastName("Smith");
        // When
        service.getUserByFirstNameAndLastName(user.getFirstName(), user.getLastName());
        // Then
        verify(esStrategy).findUserByFirstNameAndLastName(
            user.getFirstName(), user.getLastName());
        verify(dbStrategy, never()).findUserByFirstNameAndLastName(
            user.getFirstName(), user.getLastName());
    }

    @Test
    void givenEndingDayWhenGetUserByIdIsCalledThenDbRepoIsCalled(){
        // Given
        Instant instant = Instant.parse("2024-03-24T17:00:59.999Z");
        when(clock.instant()).thenReturn(instant);
        User user = new User();
        // When
        service.getUserById(user.getId());
        // Then
        verify(esStrategy, never()).findUserById(user.getId());
        verify(dbStrategy).findUserById(user.getId());
    }

    @Test
    void givenBeginningNightWhenUpdateUserThenEsRepoIsCalled(){
        // Given
        Instant instant = Instant.parse("2024-03-24T17:01:00.000Z");
        when(clock.instant()).thenReturn(instant);
        User user = new User().setFirstName("John").setLastName("Smith");
        // When
        service.updateUser(user);
        // Then
        verify(esStrategy).updateUser(user);
        verify(dbStrategy, never()).updateUser(user);
    }

    @Test
    void givenEndingDayWhenDeleteUserIsCalledThenDbRepoIsCalled(){
        // Given
        Instant instant = Instant.parse("2024-03-24T17:00:59.999Z");
        when(clock.instant()).thenReturn(instant);
        User user = new User();
        // When
        service.deleteUser(user);
        // Then
        verify(esStrategy, never()).deleteById(user.getId());
        verify(dbStrategy).deleteById(user.getId());
    }
}