package com.orvion.finance.application.mapper;

import com.orvion.finance.application.dto.response.InvoiceLineItemResponse;
import com.orvion.finance.domain.model.InvoiceLineItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceLineItemMapper {

    @Mapping(target = "subtotal", source = "subtotal.amount")
    @Mapping(target = "taxAmount", source = "taxAmount.amount")
    InvoiceLineItemResponse toResponse(InvoiceLineItem item);

    List<InvoiceLineItemResponse> toResponseList(List<InvoiceLineItem> items);
}
