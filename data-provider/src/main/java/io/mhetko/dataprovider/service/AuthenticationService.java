package io.mhetko.dataprovider.service;


import io.mhetko.dataprovider.model.CurrentUser;
import io.mhetko.dataprovider.model.builder.AuthenticationRequest;
import io.mhetko.dataprovider.model.builder.AuthenticationResponse;
import io.mhetko.dataprovider.model.entity.Token;
import io.mhetko.dataprovider.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenRepository tokenRepository;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        revokeAllUserTokens((CurrentUser) userDetails);
        saveUserToken((CurrentUser) userDetails, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse refreshToken(HttpServletRequest request){
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String refreshToken = authHeader.substring(7);
        String userName= jwtService.extractUserName(refreshToken);
        if (userName != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            if (jwtService.isTokenValidAndNotExpired(refreshToken, userDetails)) {
                String accessToken = jwtService.generateToken(userDetails);
                revokeAllUserTokens((CurrentUser) userDetails);
                saveUserToken((CurrentUser) userDetails, accessToken);
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

            }
        }
        return null;
    }


    private void revokeAllUserTokens(CurrentUser user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUser().getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(CurrentUser user, String jwtToken) {
        var token = Token.builder()
                .user(user.getUser())
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }



}

