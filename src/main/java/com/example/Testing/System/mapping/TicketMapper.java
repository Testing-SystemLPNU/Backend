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
        dto.setQuestions(
                ticket.getTicketquestions().stream()
                        .map(tq -> QuestionMapper.toResponseDto(tq.getQuestion()))
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
/*
curl -X POST https://openrouter.ai/api/v1/chat/completions \
  -H "Authorization: Bearer <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>" \
  -H "Content-Type: application/json" \
  -H "HTTP-Referer: https://lpnu-backend.ihor-shevchuk.dev" \
  -H "X-Title: AI-Question-Gen" \
  -d '{
        "model": "mistralai/mistral-7b-instruct",
        "messages": [{"role": "user", "content": "Say hi"}],
        "temperature": 0.7
      }'
 */
