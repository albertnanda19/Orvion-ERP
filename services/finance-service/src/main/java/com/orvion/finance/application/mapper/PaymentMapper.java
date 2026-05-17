package com.orvion.finance.application.mapper;

import com.orvion.finance.application.dto.response.PaymentResponse;
import com.orvion.finance.domain.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {com.orvion.finance.domain.model.enums.PaymentMethod.class})
public interface PaymentMapper {

    @Mapping(target = "method", expression = "java(payment.getMethod().name())")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "amount.currencyCode")
    PaymentResponse toResponse(Payment payment);

    List<PaymentResponse> toResponseList(List<Payment> payments);
}
