package com.murat.delivery.mapper;

import com.murat.delivery.dto.OrderRequest;
import com.murat.delivery.dto.OrderResponse;
import com.murat.delivery.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "courier", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderRequest orderRequest);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "courier.id", target = "courierId")
    OrderResponse toResponse(Order order);
}
