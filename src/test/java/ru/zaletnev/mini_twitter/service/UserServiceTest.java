package ru.zaletnev.mini_twitter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.zaletnev.mini_twitter.model.User;
import ru.zaletnev.mini_twitter.model.dto.RegisterRequest;
import ru.zaletnev.mini_twitter.model.dto.UserResponse;
import ru.zaletnev.mini_twitter.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("register: создаёт пользователя с зашифрованным паролем и возвращает UserResponse")
    void registerCreatesUserWithEncodedPassword() {
        var request = new RegisterRequest("john", "password", "John Doe");
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        var saved = User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .password("encoded")
                .displayName("John Doe")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(saved);


        UserResponse response = userService.register(request);


        assertThat(response.id()).isEqualTo(saved.getId());
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.displayName()).isEqualTo("John Doe");


        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        var created = userCaptor.getValue();
        assertThat(created.getUsername()).isEqualTo("john");
        assertThat(created.getDisplayName()).isEqualTo("John Doe");
        assertThat(created.getPassword()).isEqualTo("encoded"); // пароль прошёл через encoder


        verify(userRepository).findByUsername(eq("john"));
        verify(passwordEncoder).encode(eq("password"));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("register: бросает IllegalArgumentException если username занят")
    void registerThrowsWhenUsernameAlreadyTaken() {

        var request = new RegisterRequest("john", "password", "John Doe");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already taken"); // или .hasMessageContaining("Username")

        verify(userRepository).findByUsername("john");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }
}
