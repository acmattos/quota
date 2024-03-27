package br.com.acmattos.quota.service;

import br.com.acmattos.quota.entity.User;
import br.com.acmattos.quota.repository.UserRepositoryStrategy;

import java.util.Optional;

public interface UserPersistenceService {
    UserRepositoryStrategy getCurrentRepository();

    User createUser(User user);

    Optional<User> getUserById(String userId);

    Optional<User> getUserByFirstNameAndLastName(
        String firstName, String lastName);

    User updateUser(User user);

    User deleteUser(User user);
}
