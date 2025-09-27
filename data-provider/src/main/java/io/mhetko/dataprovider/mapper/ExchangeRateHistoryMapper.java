package io.mhetko.dataprovider.mapper;

import io.mhetko.dataprovider.dto.ExchangeRateHistoryDto;
import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeRateHistoryMapper {
    ExchangeRateHistoryDto toDto(ExchangeRateHistoryEntity entity);
    ExchangeRateHistoryEntity toEntity(ExchangeRateHistoryDto dto);
}
