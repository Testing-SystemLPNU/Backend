package com.example.Testing.System.dto.ticket;



import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketRequestDto {
    private List<Integer> questionIds;
    private String studentFullName;
    private String studentGroup;
}
