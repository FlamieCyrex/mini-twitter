package ru.zaletnev.mini_twitter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.zaletnev.mini_twitter.model.dto.CreateTweetRequest;
import ru.zaletnev.mini_twitter.model.dto.TweetDto;
import ru.zaletnev.mini_twitter.service.TweetService;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/tweets")
@RequiredArgsConstructor
public class TweetController {
    private final TweetService tweetService;

    @PostMapping
    public TweetDto create(@RequestBody @Valid CreateTweetRequest req, Authentication auth) {
        return tweetService.createTweet(auth.getName(), req);
    }

    @GetMapping
    public Page<TweetDto> getFeed(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                  Pageable pageable) {
        return tweetService.tweetFeed(pageable);
    }

    @GetMapping("/by/{username}")
    public Page<TweetDto> getTweetByUsername(@PathVariable String username,
                                             @PageableDefault Pageable pageable) {
        return tweetService.userTweets(username, pageable);
    }

    @DeleteMapping("/id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTweet(@PathVariable UUID tweetId, Authentication auth) throws AccessDeniedException {
        tweetService.deleteTweet(auth.getName(), tweetId);
    }

}
