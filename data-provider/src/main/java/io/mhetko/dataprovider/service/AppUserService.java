package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.AppUser;

public interface AppUserService {

    AppUser findByUsername(String name);

    AppUser findByEmail(String name);

    void saveUser(AppUser user);
}
