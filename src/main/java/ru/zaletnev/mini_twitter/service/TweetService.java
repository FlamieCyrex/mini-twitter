package ru.zaletnev.mini_twitter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zaletnev.mini_twitter.model.Tweet;
import ru.zaletnev.mini_twitter.model.dto.CreateTweetRequest;
import ru.zaletnev.mini_twitter.model.dto.TweetDto;
import ru.zaletnev.mini_twitter.repository.TweetRepository;
import ru.zaletnev.mini_twitter.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TweetService {
    private final TweetRepository tweets;
    private final UserRepository users;

    @Transactional
    public TweetDto createTweet(String username, CreateTweetRequest request) {
        var author = users.findByUsername(username).orElseThrow();
        var tweet = Tweet.builder()
                .author(author)
                .content(request.content())
                .build();
        var savedTweet = tweets.save(tweet);
        return toTweetDto(savedTweet);
    }

    @Transactional(readOnly = true)
    public Page<TweetDto> tweetFeed(Pageable pageable) {
        return tweets.findAllByOrderByCreatedAtDesc(pageable).map(this::toTweetDto);
    }

    @Transactional(readOnly = true)
    public Page<TweetDto> userTweets(String username, Pageable pageable) {
        return tweets.findByAuthor_UsernameOrderByCreatedAtDesc(username, pageable).map(this::toTweetDto);
    }

    @Transactional
    public void deleteTweet(String username, UUID tweetId) throws AccessDeniedException {
        var t = tweets.findById(tweetId).orElseThrow();
        if (!t.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("Not your tweet!");
        }
        tweets.delete(t);
    }

    private TweetDto toTweetDto(Tweet tweet) {
        return new TweetDto(
                tweet.getId().toString(),
                tweet.getAuthor().getUsername(),
                tweet.getContent(),
                tweet.getCreatedAt().toString()
        );
    }


}
