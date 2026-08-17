package com.finflow.application.mapper;

import com.finflow.application.dto.customer.AddressDto;
import com.finflow.application.dto.customer.CustomerResponse;
import com.finflow.domain.model.customer.Address;
import com.finflow.domain.model.customer.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);

    AddressDto toDto(Address address);

    Address toEntity(AddressDto dto);
}
