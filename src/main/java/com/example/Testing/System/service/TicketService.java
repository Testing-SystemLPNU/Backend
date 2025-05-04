package com.example.Testing.System.service;

import com.example.Testing.System.dto.ticket.TicketRequestDto;
import com.example.Testing.System.model.Ticket;

import java.io.IOException;
import java.util.List;

public interface TicketService {
    Ticket create(Integer courseId, TicketRequestDto dto, String username);
    List<Ticket> getAll(Integer courseId, String username);
    Ticket getById(Integer id, String username);
    Ticket update(Integer id, TicketRequestDto dto, String username);
    void delete(Integer id, String username);
    byte[] generatePdf(Integer id, String username) throws IOException;
}
