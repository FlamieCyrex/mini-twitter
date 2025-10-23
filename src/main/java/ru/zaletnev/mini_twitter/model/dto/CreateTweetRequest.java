package ru.zaletnev.mini_twitter.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTweetRequest(
        @NotBlank @Size(max = 260) String content
) {
}
