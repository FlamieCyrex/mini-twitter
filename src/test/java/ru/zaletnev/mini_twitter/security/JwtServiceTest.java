package ru.zaletnev.mini_twitter.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=T6E1eZnhZ70y351+7zastzgQtYfpDk7s72hRisYA0Q709GxxblKGOjZKLY7/i0pQIgjMxaUW3skEgCNqByUNbQ==",
        "jwt.exptime=340000"
})
class JwtServiceTest {
    @Autowired
    JwtService jwtService;

    @Test
    void generateAndExtractUsername(){
        String token = jwtService.generateToken("Artem");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("Artem");
    }

}