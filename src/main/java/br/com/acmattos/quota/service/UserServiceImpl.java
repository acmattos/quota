package br.com.acmattos.quota.service;

import br.com.acmattos.quota.entity.User;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserPersistenceService persistenceService;

    public UserServiceImpl(final UserPersistenceService persistenceService) {
        this.persistenceService = Objects.requireNonNull(persistenceService);
    }

    @Override
    public Optional<User> createUser(final User user) {
         return persistenceService.getUserByFirstNameAndLastName(
                user.getFirstName(), user.getLastName())
            .<Optional<User>>map(userFound -> {
                LOGGER.info("Can´t create user: already exists!");
                return Optional.empty();
            })
            .orElseGet(() -> {
                 LOGGER.info("User created!");
                 return Optional.of(persistenceService.createUser(user));
            });
    }

    @Override
    public Optional<User> getUserById(@Nonnull final String userId) {
        return persistenceService.getUserById(userId);
    }

    @Override
    public Optional<User> updateUser(String userId, User updatedUser) {
        return persistenceService.getUserById(userId)
            .map(userFound -> {
                userFound.setFirstName(updatedUser.getFirstName());
                userFound.setLastName(updatedUser.getLastName());
                userFound.setLastLoginTimeUtc(userFound.getLastLoginTimeUtc());
                LOGGER.info("User updated!");
                return persistenceService.updateUser(userFound);
            });
    }

    @Override
    public Optional<User> deleteUserById(String userId){
        return persistenceService.getUserById(userId)
            .map(userFound -> {
                LOGGER.info("User deleted!");
                return persistenceService.deleteUser(userFound);
            });
    }
}
