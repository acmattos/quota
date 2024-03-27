package br.com.acmattos.quota.service;

import br.com.acmattos.quota.entity.User;
import br.com.acmattos.quota.repository.UserRepositoryDatabaseStrategy;
import br.com.acmattos.quota.repository.UserRepositoryElasticSearchStrategy;
import br.com.acmattos.quota.repository.UserRepositoryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceServiceImpl implements UserPersistenceService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(UserPersistenceServiceImpl.class);
    private static LocalTime START_TIME = LocalTime.of(8, 59, 59, 999999999);
    private static LocalTime END_TIME = LocalTime.of(17, 1, 0);
    private final Clock clock;
    private final UserRepositoryElasticSearchStrategy esStrategy;
    private final UserRepositoryDatabaseStrategy dbStrategy;

    public UserPersistenceServiceImpl(
        final Clock clock,
        final UserRepositoryElasticSearchStrategy esStrategy,
        final UserRepositoryDatabaseStrategy dbStrategy
    ) {
        this.clock = clock;
        this.esStrategy = esStrategy;
        this.dbStrategy = dbStrategy;
    }

    @Override
    public User createUser(User user) {
        return getCurrentRepository().createUser(
            user.setId(UUID.randomUUID().toString()));
    }

    @Override
    public Optional<User> getUserByFirstNameAndLastName(
        String firstName, String lastName) {
        return getCurrentRepository()
            .findUserByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return getCurrentRepository().findUserById(userId);
    }

    @Override
    public User updateUser(User updatedUser) {
        return getCurrentRepository().updateUser(updatedUser);
    }

    @Override
    public User deleteUser(User user) {
        getCurrentRepository().deleteById(user.getId());
        return user;
    }

    @Override
    public UserRepositoryStrategy getCurrentRepository() {
        return mustUseDbRepository() ? dbStrategy : esStrategy;
    }

    /**
     *  From (9:00 - 17:00 UTC), we use MySQL as a source.
     *  During the night (17:01 - 8:59), elasticsearch.
     * @return
     */
    protected boolean mustUseDbRepository() {
        ZonedDateTime zonedDateTime =
            clock.instant().atZone(ZoneId.of("UTC"));
        LocalTime currentTime = zonedDateTime.toLocalTime();

        boolean mustUseDbRepository = currentTime
            .isAfter(START_TIME) && currentTime.isBefore(END_TIME);

        if (mustUseDbRepository) {
            LOGGER.debug("Must use database repository: " + currentTime);
        } else {
            LOGGER.debug("Must use elasticsearch repository: " + currentTime);
        }
        return mustUseDbRepository;
    }
}
