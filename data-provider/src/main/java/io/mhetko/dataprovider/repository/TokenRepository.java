package io.mhetko.dataprovider.repository;

import io.mhetko.dataprovider.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :id AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokenByUser(@Param("id") UUID id);

    Optional<Token> findByToken(String token);
}
