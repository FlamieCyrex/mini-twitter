package ru.zaletnev.mini_twitter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zaletnev.mini_twitter.model.dto.UserResponse;
import ru.zaletnev.mini_twitter.repository.UserRepository;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserResponse getProfile(Authentication authentication) {
        System.out.println(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Authorities: " + authentication.getAuthorities());
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName()
        );

    }

}
