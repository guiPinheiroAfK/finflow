package com.finflow.application.mapper;

import com.finflow.application.dto.receivable.ReceivableResponse;
import com.finflow.domain.model.receivable.Receivable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceivableMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    ReceivableResponse toResponse(Receivable receivable);
}
