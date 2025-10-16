package ru.zaletnev.mini_twitter.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 3, max = 50) String password,
        @Size(max = 50) String displayName

) {
}
