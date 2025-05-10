package com.example.Testing.System.dto.ticket;


import com.example.Testing.System.dto.question.QuestionResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
@Getter
@Setter
public class TicketResponseDto {
    private Integer id;
    private Integer ticketNumber;
    private Instant createdAt;
    private Integer courseId;
    private String studentFullName;
    private String studentGroup;
    private List<QuestionResponseDto> questions;
}
