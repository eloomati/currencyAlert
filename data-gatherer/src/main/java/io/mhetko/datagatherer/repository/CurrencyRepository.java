package io.mhetko.datagatherer.repository;

import io.mhetko.datagatherer.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
    Optional<CurrencyEntity> findByCode(String code);
}

