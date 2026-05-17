package com.orvion.finance.application.mapper;

import com.orvion.finance.application.dto.response.JournalEntryLineResponse;
import com.orvion.finance.domain.model.JournalEntryLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JournalEntryLineMapper {

    @Mapping(target = "side", expression = "java(line.getSide().name())")
    @Mapping(target = "currency", source = "amount.currencyCode")
    @Mapping(target = "amount", source = "amount.amount")
    JournalEntryLineResponse toResponse(JournalEntryLine line);

    List<JournalEntryLineResponse> toResponseList(List<JournalEntryLine> lines);
}
