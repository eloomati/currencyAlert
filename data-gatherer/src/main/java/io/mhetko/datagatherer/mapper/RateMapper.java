package io.mhetko.datagatherer.mapper;

import io.mhetko.datagatherer.dto.RateDto;
import io.mhetko.datagatherer.model.CurrencyEntity;
import io.mhetko.datagatherer.model.RateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RateMapper {
    RateMapper INSTANCE = Mappers.getMapper(RateMapper.class);

    @Mapping(target = "base", source = "base")
    @Mapping(target = "target", source = "target")
    @Mapping(target = "rate", source = "dto.rate")
    @Mapping(target = "asOf", source = "dto.asOf")
    RateEntity toEntity(RateDto dto, CurrencyEntity base, CurrencyEntity target);

    @Mapping(target = "base", source = "base.code")
    @Mapping(target = "target", source = "target.code")
    RateDto toDto(RateEntity entity);
}

