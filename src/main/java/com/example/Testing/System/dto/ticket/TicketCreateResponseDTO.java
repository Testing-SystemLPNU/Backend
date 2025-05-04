package com.example.Testing.System.dto.ticket;


import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class TicketCreateResponseDTO {
    private Integer id;
    private Integer ticketNumber;
    private Instant createdAt;
    private Integer courseId;
    private String message;
}
