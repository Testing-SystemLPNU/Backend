package com.example.Testing.System.service;

import com.example.Testing.System.dto.check.CheckTicketRequestDto;
import com.example.Testing.System.dto.check.CheckhistoryResultDto;
import com.example.Testing.System.model.Checkhistory;

public interface CheckHistotyService {
    CheckhistoryResultDto checkTicket(CheckTicketRequestDto dto, String username);
}

