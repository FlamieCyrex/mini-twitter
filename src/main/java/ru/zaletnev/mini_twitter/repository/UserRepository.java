package ru.zaletnev.mini_twitter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zaletnev.mini_twitter.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
