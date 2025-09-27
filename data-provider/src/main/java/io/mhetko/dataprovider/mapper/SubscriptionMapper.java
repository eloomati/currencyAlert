package io.mhetko.dataprovider.mapper;

import io.mhetko.dataprovider.dto.SubscriptionDto;
import io.mhetko.dataprovider.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    @Mapping(source = "user.id", target = "userId")
    SubscriptionDto toDto(Subscription entity);
}
