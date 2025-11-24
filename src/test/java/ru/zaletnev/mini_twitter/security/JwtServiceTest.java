package ru.zaletnev.mini_twitter.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;


class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService,
                "secretBase64",
                "T6E1eZnhZ70y351+7zastzgQtYfpDk7s72hRisYA0Q709GxxblKGOjZKLY7/i0pQIgjMxaUW3skEgCNqByUNbQ==");

        ReflectionTestUtils.setField(jwtService,
                "expTime",
                3600000L);
        jwtService.initKey();
    }

    @Test
    void generateAndExtractUsername() {
        String token = jwtService.generateToken("Jefrey");
        assertThat(token).isNotBlank();

        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("Jefrey");

        Claims claims = jwtService.extractClaim(token, c -> c);
        assertThat(claims.getExpiration()).isNotNull();

    }


}