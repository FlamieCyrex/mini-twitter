package ru.zaletnev.mini_twitter.model.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String displayName
) {
}
