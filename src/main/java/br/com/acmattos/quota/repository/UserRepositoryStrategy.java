package br.com.acmattos.quota.repository;

import br.com.acmattos.quota.entity.User;

import java.util.Optional;

public interface UserRepositoryStrategy {
    User createUser(User user);

    Optional<User> findUserById(String userId);

    Optional<User> findUserByFirstNameAndLastName(
        String firstName, String lastName);

    User updateUser(User updatedUser);

    void deleteById(String userId);
}
