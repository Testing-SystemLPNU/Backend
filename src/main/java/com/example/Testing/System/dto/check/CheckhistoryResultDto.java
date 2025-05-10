package com.example.Testing.System.dto.check;

import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
public class CheckhistoryResultDto {
    private Integer score;
    private Integer total;
//    private Map<String, Object> correctAnswers;
//    private Map<String, Object> yourAnswers;
    private Instant createdAt;
}
