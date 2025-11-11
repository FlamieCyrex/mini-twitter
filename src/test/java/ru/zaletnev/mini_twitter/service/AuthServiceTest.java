package ru.zaletnev.mini_twitter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.zaletnev.mini_twitter.model.dto.LoginRequest;
import ru.zaletnev.mini_twitter.model.dto.TokenResponse;
import ru.zaletnev.mini_twitter.security.JwtService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("login(auth): проверка параметров токена")
    void loginAuthenticatesAndReturnsTokenResponse() {
        LoginRequest request = new LoginRequest("john", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken("john")).thenReturn("token");
        when(jwtService.getExpTime()).thenReturn(3600L);

        TokenResponse response = authService.login(request);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        Authentication token = captor.getValue();
        assertThat(token).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(token.getName()).isEqualTo("john");
        assertThat(token.getCredentials()).isEqualTo("password");

        verify(jwtService).generateToken("john");
        assertThat(response.response()).isEqualTo("token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expTimeInMillis()).isEqualTo(3600L);
    }
}
