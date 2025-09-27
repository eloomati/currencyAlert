package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.CurrentUser;
import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.model.builder.AuthenticationRequest;
import io.mhetko.dataprovider.model.builder.AuthenticationResponse;
import io.mhetko.dataprovider.model.entity.Token;
import io.mhetko.dataprovider.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private UserDetailsServiceImpl userDetailsService;
    private TokenRepository tokenRepository;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);
        tokenRepository = mock(TokenRepository.class);
        authenticationService = new AuthenticationService(jwtService, authenticationManager, userDetailsService, tokenRepository);
    }

    @Test
    void shouldAuthenticateAndReturnTokens() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("user")
                .password("pass")
                .build();

        CurrentUser currentUser = mock(CurrentUser.class);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(currentUser);
        when(jwtService.generateToken(currentUser)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(currentUser)).thenReturn("refreshToken");
        when(currentUser.getUser()).thenReturn(new AppUser());

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void shouldRefreshTokenAndReturnNewAccessToken() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer refreshToken");

        when(jwtService.extractUserName("refreshToken")).thenReturn("user");
        CurrentUser currentUser = mock(CurrentUser.class);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(currentUser);
        when(jwtService.isTokenValidAndNotExpired("refreshToken", currentUser)).thenReturn(true);
        when(jwtService.generateToken(currentUser)).thenReturn("newAccessToken");
        when(currentUser.getUser()).thenReturn(new AppUser());

        AuthenticationResponse response = authenticationService.refreshToken(httpRequest);

        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void shouldReturnNullWhenRefreshTokenIsInvalid() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalidToken");
        when(jwtService.extractUserName("invalidToken")).thenReturn(null);

        AuthenticationResponse response = authenticationService.refreshToken(httpRequest);

        assertThat(response).isNull();
    }
}