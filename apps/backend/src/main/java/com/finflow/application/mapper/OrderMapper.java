package com.finflow.application.mapper;

import com.finflow.application.dto.order.OrderItemResponse;
import com.finflow.application.dto.order.OrderResponse;
import com.finflow.domain.model.order.Order;
import com.finflow.domain.model.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "quote.id", target = "quoteId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "seller.name", target = "sellerName")
    @Mapping(source = "items", target = "items")
    OrderResponse toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toItemResponse(OrderItem item);
}
