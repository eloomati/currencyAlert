package io.mhetko.dataprovider.repository;

import io.mhetko.dataprovider.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    AppUser findByUsername(String username);

    AppUser findByEmail(String email);
}
