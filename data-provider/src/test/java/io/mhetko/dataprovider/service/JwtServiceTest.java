package io.mhetko.dataprovider.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotBlank();

        boolean valid = jwtService.isTokenValidAndNotExpired(token, userDetails);
        assertThat(valid).isTrue();
    }

    @Test
    void shouldExtractUserNameFromToken() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUserName(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void shouldDetectExpiredToken() throws InterruptedException {
        String token = jwtService.generateToken(Collections.emptyMap(), userDetails, 1); // 1 ms
        Thread.sleep(5);
        boolean valid = jwtService.isTokenValidAndNotExpired(token, userDetails);
        assertThat(valid).isFalse();
    }

    @Test
    void shouldGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        assertThat(refreshToken).isNotBlank();
        String username = jwtService.extractUserName(refreshToken);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        String invalidToken = "abc.def.ghi";
        boolean valid = jwtService.isTokenValidAndNotExpired(invalidToken, userDetails);
        assertThat(valid).isFalse();
    }

    @Test
    void shouldReturnFalseForTokenWithDifferentUsername() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = User.withUsername("otheruser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        boolean valid = jwtService.isTokenValidAndNotExpired(token, otherUser);
        assertThat(valid).isFalse();
    }
}