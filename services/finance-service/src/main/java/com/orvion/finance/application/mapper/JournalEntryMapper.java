package com.orvion.finance.application.mapper;

import com.orvion.finance.application.dto.response.JournalEntryResponse;
import com.orvion.finance.domain.model.JournalEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JournalEntryLineMapper.class})
public interface JournalEntryMapper {

    @Mapping(target = "status", expression = "java(entry.getStatus().name())")
    @Mapping(target = "totalDebits", expression = "java(entry.getTotalDebits().getAmount())")
    @Mapping(target = "totalCredits", expression = "java(entry.getTotalCredits().getAmount())")
    @Mapping(target = "lines", source = "lines")
    JournalEntryResponse toResponse(JournalEntry entry);

    List<JournalEntryResponse> toResponseList(List<JournalEntry> entries);
}
