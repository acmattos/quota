package br.com.acmattos.quota.repository;

import br.com.acmattos.quota.entity.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class UserRepositoryElasticSearchStrategy
    implements UserRepositoryStrategy {
    private final ConcurrentMap<String, User> elasticsearch =
        new ConcurrentHashMap<>();

    @Override
    public User createUser(User user) {
        elasticsearch.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(
            elasticsearch.getOrDefault(userId, null));
    }

    @Override
    public Optional<User> findUserByFirstNameAndLastName(
        String firstName, String lastName) {
        return elasticsearch
            .values()
            .stream()
            .filter(user -> user.getFirstName().equals(firstName)
                && user.getLastName().equals(lastName))
            .findFirst();
    }

    @Override
    public User updateUser(User updatedUser) {
        return createUser(updatedUser);
    }

    @Override
    public void deleteById(String userId) {
        elasticsearch.remove(userId);
    }
}
