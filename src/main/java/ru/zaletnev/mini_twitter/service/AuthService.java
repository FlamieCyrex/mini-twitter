package ru.zaletnev.mini_twitter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.zaletnev.mini_twitter.model.dto.LoginRequest;
import ru.zaletnev.mini_twitter.model.dto.TokenResponse;
import ru.zaletnev.mini_twitter.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public TokenResponse login(LoginRequest loginRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken
                (loginRequest.username(), loginRequest.password());
        authenticationManager.authenticate(authentication);
        String token = jwtService.generateToken(loginRequest.username());
        return new TokenResponse(token, "Bearer", jwtService.getExpTime());
    }


}
