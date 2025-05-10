package com.example.Testing.System.dto.check;

import lombok.Data;

import java.util.Map;

@Data
public class CheckTicketRequestDto {
    private Integer ticketId;
    private Map<String, String> answers; // questionId -> selectedOption (A/B/C/D)
}
