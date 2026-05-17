package com.orvion.finance.application.mapper;

import com.orvion.finance.application.dto.response.InvoiceResponse;
import com.orvion.finance.domain.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InvoiceLineItemMapper.class})
public interface InvoiceMapper {

    @Mapping(target = "type", expression = "java(invoice.getType().name())")
    @Mapping(target = "status", expression = "java(invoice.getStatus().name())")
    @Mapping(target = "subtotal", source = "subtotal.amount")
    @Mapping(target = "taxAmount", source = "taxAmount.amount")
    @Mapping(target = "totalAmount", source = "totalAmount.amount")
    @Mapping(target = "paidAmount", source = "paidAmount.amount")
    @Mapping(target = "outstandingAmount", source = "outstandingAmount.amount")
    @Mapping(target = "lineItems", source = "lineItems")
    InvoiceResponse toResponse(Invoice invoice);

    List<InvoiceResponse> toResponseList(List<Invoice> invoices);
}
