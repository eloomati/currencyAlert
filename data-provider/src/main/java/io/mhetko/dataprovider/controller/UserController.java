package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.service.AppUserServiceImpl;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AppUserServiceImpl appUserService;

    @PostMapping
    @Operation(
            summary = "Rejestracja nowego użytkownika",
            description = "Tworzy nowego użytkownika na podstawie przesłanych danych.",
            tags = {"User"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pomyślnie utworzono użytkownika",
                    content = @Content(schema = @Schema(implementation = AppUser.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe"
            )
    })
    public AppUser save(@RequestBody AppUser securityUser) {
        appUserService.saveUser(securityUser);
        return securityUser;
    }
}