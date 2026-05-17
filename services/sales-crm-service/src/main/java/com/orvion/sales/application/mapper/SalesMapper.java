package com.orvion.sales.application.mapper;

import com.orvion.sales.application.dto.response.*;
import com.orvion.sales.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = {java.math.BigDecimal.class})
public interface SalesMapper {

    @Mapping(target = "source", expression = "java(lead.getSource().name())")
    @Mapping(target = "status", expression = "java(lead.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    LeadResponse toLeadResponse(Lead lead);

    List<LeadResponse> toLeadResponseList(List<Lead> leads);

    @Mapping(target = "stage", expression = "java(opp.getStage().name())")
    @Mapping(target = "expectedValue", expression = "java(opp.getExpectedValue() != null ? opp.getExpectedValue().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "currency", expression = "java(opp.getExpectedValue() != null ? opp.getExpectedValue().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "createdAt", source = "createdAt")
    OpportunityResponse toOpportunityResponse(Opportunity opp);

    List<OpportunityResponse> toOpportunityResponseList(List<Opportunity> opportunities);

    @Mapping(target = "creditLimit", expression = "java(cust.getCreditLimit() != null ? cust.getCreditLimit().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "creditCurrency", expression = "java(cust.getCreditLimit() != null ? cust.getCreditLimit().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "outstanding", expression = "java(cust.getOutstandingBalance() != null ? cust.getOutstandingBalance().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "outstandingCurrency", expression = "java(cust.getOutstandingBalance() != null ? cust.getOutstandingBalance().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "customerType", expression = "java(cust.getCustomerType().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    CustomerResponse toCustomerResponse(Customer cust);

    List<CustomerResponse> toCustomerResponseList(List<Customer> customers);

    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    @Mapping(target = "totalAmount", expression = "java(order.getTotalAmount() != null ? order.getTotalAmount().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "currency", expression = "java(order.getTotalAmount() != null ? order.getTotalAmount().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "lines", expression = "java(toSalesOrderLineResponseList(order.getLines()))")
    SalesOrderResponse toSalesOrderResponse(SalesOrder order);

    List<SalesOrderResponse> toSalesOrderResponseList(List<SalesOrder> orders);

    @Mapping(target = "unitPrice", expression = "java(line.getUnitPrice() != null ? line.getUnitPrice().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "unitCurrency", expression = "java(line.getUnitPrice() != null ? line.getUnitPrice().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "lineTotal", expression = "java(line.getLineTotal() != null ? line.getLineTotal().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "lineCurrency", expression = "java(line.getLineTotal() != null ? line.getLineTotal().getCurrencyCode() : \"IDR\")")
    SalesOrderResponse.SalesOrderLineResponse toSalesOrderLineResponse(SalesOrderLine line);

    List<SalesOrderResponse.SalesOrderLineResponse> toSalesOrderLineResponseList(List<SalesOrderLine> lines);
}
