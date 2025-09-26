package io.mhetko.datagatherer.repository;

import io.mhetko.datagatherer.model.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<RateEntity, Long> {
}
