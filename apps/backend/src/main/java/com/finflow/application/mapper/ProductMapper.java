package com.finflow.application.mapper;

import com.finflow.application.dto.product.ProductResponse;
import com.finflow.domain.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    ProductResponse toResponse(Product product);
}
