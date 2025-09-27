package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.builder.AuthenticationRequest;
import io.mhetko.dataprovider.model.builder.AuthenticationResponse;
import io.mhetko.dataprovider.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Logs in a user based on username and password. Returns a JWT token upon successful authentication.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged in, returns JWT token",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid login credentials"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh JWT token",
            description = "Refreshes the JWT token for the authenticated user.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully refreshed JWT token",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired token"
            )
    })
    public ResponseEntity<AuthenticationResponse>  refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}