package ru.zaletnev.mini_twitter.model.dto;

public record TokenResponse(
        String response,
        String tokenType,
        long expTimeInMillis
) {
}
