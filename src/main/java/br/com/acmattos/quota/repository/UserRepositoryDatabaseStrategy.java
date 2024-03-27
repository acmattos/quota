package br.com.acmattos.quota.repository;

import br.com.acmattos.quota.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryDatabaseStrategy
    extends UserRepositoryStrategy, JpaRepository<User, String> {
    default User createUser(User user){
        return save(user);
    }

    default User updateUser(User updatedUser){
        return save(updatedUser);
    }

    default void deleteById(String userId){
        deleteById(userId);
    }
}
