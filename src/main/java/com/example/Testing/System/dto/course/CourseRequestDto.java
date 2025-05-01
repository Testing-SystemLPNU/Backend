package com.example.Testing.System.dto.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CourseRequestDto {
    private String title;
    private String description;
}
