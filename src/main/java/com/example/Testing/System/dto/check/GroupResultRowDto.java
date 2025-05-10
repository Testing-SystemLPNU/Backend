package com.example.Testing.System.dto.check;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupResultRowDto {
    private String studentName;
    private Integer ticketId;
    private int score;
    private int maxScore;
}
