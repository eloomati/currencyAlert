package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.service.AppUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AppUserServiceImpl appUserService;

    @PostMapping
    public AppUser save(@RequestBody AppUser securityUser) {
        appUserService.saveUser(securityUser);
        return securityUser;
    }
}
