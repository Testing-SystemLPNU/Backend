package com.example.Testing.System.service;

import com.example.Testing.System.dto.check.CheckTicketRequestDto;
import com.example.Testing.System.dto.check.CheckhistoryResultDto;
import com.example.Testing.System.dto.check.GroupResultRowDto;
import com.example.Testing.System.model.Checkhistory;

import java.io.IOException;
import java.util.List;

public interface CheckHistotyService {
    CheckhistoryResultDto checkTicket(CheckTicketRequestDto dto, String username);
    List<GroupResultRowDto> getGroupResultsTable(String groupName);
    byte[] generateGroupResultsPdf(List<GroupResultRowDto> results, String groupName) throws IOException;
}

