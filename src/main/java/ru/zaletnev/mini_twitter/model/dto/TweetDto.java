package ru.zaletnev.mini_twitter.model.dto;

public record TweetDto(
        String id,
        String authorUsername,
        String content,
        String createdAt
) {
}
