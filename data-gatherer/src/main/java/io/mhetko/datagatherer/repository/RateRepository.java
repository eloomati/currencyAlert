package io.mhetko.datagatherer.repository;

import io.mhetko.datagatherer.model.RateEntity;
import io.mhetko.datagatherer.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateRepository extends JpaRepository<RateEntity, Long> {
    Optional<RateEntity> findByBaseAndTarget(CurrencyEntity base, CurrencyEntity target);
}
