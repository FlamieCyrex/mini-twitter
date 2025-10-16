package ru.zaletnev.mini_twitter.model.dto;

import java.util.UUID;

public record UserResponce(
        UUID id,
        String username,
        String displayName
) {
}
