package com.finflow.application.mapper;

import com.finflow.application.dto.quote.QuoteItemResponse;
import com.finflow.application.dto.quote.QuoteResponse;
import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.model.quote.QuoteItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "seller.name", target = "sellerName")
    @Mapping(source = "items", target = "items")
    QuoteResponse toResponse(Quote quote);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    QuoteItemResponse toItemResponse(QuoteItem item);
}
