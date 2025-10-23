package ru.zaletnev.mini_twitter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zaletnev.mini_twitter.model.Tweet;

import java.util.UUID;


public interface TweetRepository extends JpaRepository<Tweet, UUID> {
    Page<Tweet> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Tweet> findByAuthor_UsernameOrderByCreatedAtDesc(String username, Pageable pageable);
}
