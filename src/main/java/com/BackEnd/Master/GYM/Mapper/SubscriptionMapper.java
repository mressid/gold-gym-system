package com.BackEnd.Master.GYM.Mapper;

import com.BackEnd.Master.GYM.dto.SubscriptionDto;
import com.BackEnd.Master.GYM.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(target = "valid", expression = "java(entity.isValid())")
    SubscriptionDto entityToDto(Subscription entity);

    List<SubscriptionDto> entityToDto(List<Subscription> entities);

    @Mapping(target = "customer", ignore = true)
    Subscription dtoToEntity(SubscriptionDto dto);
}
