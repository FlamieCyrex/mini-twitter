package ru.zaletnev.mini_twitter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.zaletnev.mini_twitter.model.dto.LoginRequest;
import ru.zaletnev.mini_twitter.model.dto.RegisterRequest;
import ru.zaletnev.mini_twitter.model.dto.TokenResponse;
import ru.zaletnev.mini_twitter.model.dto.UserResponse;
import ru.zaletnev.mini_twitter.service.AuthService;
import ru.zaletnev.mini_twitter.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
