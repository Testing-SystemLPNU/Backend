package com.example.Testing.System.mapping;

import com.example.Testing.System.dto.ticket.TicketCreateResponseDTO;
import com.example.Testing.System.dto.ticket.TicketRequestDto;
import com.example.Testing.System.dto.ticket.TicketResponseDto;
import com.example.Testing.System.model.Ticket;

import java.util.stream.Collectors;

public class TicketMapper {

    public static Ticket toEntity(TicketRequestDto dto) {
        Ticket ticket = new Ticket();
        // Тут можна додати базові поля з dto, якщо потрібно.
        return ticket;
    }

    public static TicketResponseDto toDto(Ticket ticket) {
        TicketResponseDto dto = new TicketResponseDto();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setCourseId(ticket.getCourse().getId());
        if (ticket.getStudent() != null) {
            dto.setStudentFullName(ticket.getStudent().getFullName());
            dto.setStudentGroup(ticket.getStudent().getGroupName());
        }

        dto.setQuestionIds(
            ticket.getTicketquestions().stream()
                    .map(tq -> QuestionMapper.toResponseDto(tq.getQuestion()).getId())
                    .collect(Collectors.toList())
        );
        return dto;
    }

    public static TicketCreateResponseDTO toCreateDto(Ticket ticket, String message) {
        if (ticket == null) {
            TicketCreateResponseDTO errorDto = new TicketCreateResponseDTO();
            errorDto.setMessage("Something went wrong");
            return errorDto;
        }

        TicketCreateResponseDTO dto = new TicketCreateResponseDTO();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setCourseId(ticket.getCourse().getId());
        dto.setMessage(message);

        if (ticket.getStudent() != null) {
            dto.setStudentFullName(ticket.getStudent().getFullName());
            dto.setStudentGroup(ticket.getStudent().getGroupName());
        }

        return dto;
    }
}