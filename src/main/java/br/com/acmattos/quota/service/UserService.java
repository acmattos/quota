package br.com.acmattos.quota.service;

import br.com.acmattos.quota.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> createUser(User user);

    Optional<User> getUserById(String userId);

    Optional<User> updateUser(String userId, User updatedUser);

    Optional<User> deleteUserById(String userId);
}
