package com.finflow.application.mapper;

import com.finflow.application.dto.supplier.SupplierResponse;
import com.finflow.domain.model.supplier.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponse toResponse(Supplier supplier);
}
