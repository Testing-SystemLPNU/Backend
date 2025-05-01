package com.example.Testing.System.controller;

import com.example.Testing.System.dto.course.CourseRequestDto;
import com.example.Testing.System.dto.course.CourseResponseDto;
import com.example.Testing.System.model.Course;
import com.example.Testing.System.service.CourseService;
import com.example.Testing.System.mapping.CourseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @RequestBody CourseRequestDto course,
            @AuthenticationPrincipal UserDetails userDetails) {

        Course saved = courseService.createCourse(course, userDetails.getUsername());
        return ResponseEntity.ok(CourseMapper.toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses(@AuthenticationPrincipal UserDetails userDetails) {
        List<CourseResponseDto> dtoList = courseService.getAllCourses(userDetails.getUsername())
                .stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Course course = courseService.getCourseById(id, userDetails.getUsername());
        return ResponseEntity.ok(CourseMapper.toDto(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Integer id,
            @RequestBody Course course,
            @AuthenticationPrincipal UserDetails userDetails) {

        Course updated = courseService.updateCourse(id, course, userDetails.getUsername());
        return ResponseEntity.ok(CourseMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        courseService.deleteCourse(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
