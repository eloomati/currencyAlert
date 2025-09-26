package io.mhetko.datagatherer.mapper;

import io.mhetko.datagatherer.dto.ExchangeRateHistoryDto;
import io.mhetko.datagatherer.model.ExchangeRateHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeRateHistoryMapper {
    ExchangeRateHistoryDto toDto(ExchangeRateHistoryEntity entity);
    ExchangeRateHistoryEntity toEntity(ExchangeRateHistoryDto dto);
}
