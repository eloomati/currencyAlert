package io.mhetko.datagatherer.mapper;

import io.mhetko.datagatherer.dto.RateDto;
import io.mhetko.datagatherer.model.CurrencyEntity;
import io.mhetko.datagatherer.model.RateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rate",  source = "dto.rate")
    @Mapping(target = "asOf",  source = "dto.asOf")
    @Mapping(target = "base",  source = "base")
    @Mapping(target = "target", source = "target")
    RateEntity toEntity(RateDto dto, CurrencyEntity base, CurrencyEntity target);

    @Mapping(target = "base",   source = "entity.base.code")
    @Mapping(target = "target", source = "entity.target.code")
    @Mapping(target = "rate",   source = "entity.rate")
    @Mapping(target = "asOf",   source = "entity.asOf")
    RateDto toDto(RateEntity entity);
}

