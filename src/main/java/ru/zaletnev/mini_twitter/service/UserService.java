package ru.zaletnev.mini_twitter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.zaletnev.mini_twitter.model.User;
import ru.zaletnev.mini_twitter.model.dto.RegisterRequest;
import ru.zaletnev.mini_twitter.model.dto.UserResponse;
import ru.zaletnev.mini_twitter.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username already taken");
                });

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .build();

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getDisplayName());

    }
}
