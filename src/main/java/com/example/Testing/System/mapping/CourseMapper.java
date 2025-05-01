package com.example.Testing.System.mapping;

import com.example.Testing.System.dto.course.CourseResponseDto;
import com.example.Testing.System.model.Course;

public class CourseMapper {

    public static CourseResponseDto toDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCreatedAt()
        );
    }
}