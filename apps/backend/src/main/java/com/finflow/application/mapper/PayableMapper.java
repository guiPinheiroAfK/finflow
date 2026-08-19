package com.finflow.application.mapper;

import com.finflow.application.dto.payable.PayableResponse;
import com.finflow.domain.model.payable.Payable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PayableMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    PayableResponse toResponse(Payable payable);
}
