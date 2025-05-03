package com.example.Testing.System.controller;

import com.example.Testing.System.constant.HttpStatuses;
import com.example.Testing.System.dto.course.CourseRequestDto;
import com.example.Testing.System.dto.course.CourseResponseDto;
import com.example.Testing.System.model.Course;
import com.example.Testing.System.service.CourseService;
import com.example.Testing.System.mapping.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@Tag(name = "Courses", description = "Endpoints for course management")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Create a course", description = "Creates a new course and assigns it to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @RequestBody CourseRequestDto course,
            @AuthenticationPrincipal UserDetails userDetails) {

        Course saved = courseService.createCourse(course, userDetails.getUsername());
        return ResponseEntity.ok(CourseMapper.toDto(saved));
    }

    @Operation(summary = "Get all courses", description = "Retrieves all courses associated with the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses(@AuthenticationPrincipal UserDetails userDetails) {
        List<CourseResponseDto> dtoList = courseService.getAllCourses(userDetails.getUsername())
                .stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @Operation(summary = "Get course by ID", description = "Retrieves a specific course by its ID for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Course course = courseService.getCourseById(id, userDetails.getUsername());
        return ResponseEntity.ok(CourseMapper.toDto(course));
    }

    @Operation(summary = "Update course", description = "Updates a specific course owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Integer id,
            @RequestBody Course course,
            @AuthenticationPrincipal UserDetails userDetails) {

        Course updated = courseService.updateCourse(id, course, userDetails.getUsername());
        return ResponseEntity.ok(CourseMapper.toDto(updated));
    }

    @Operation(summary = "Delete course", description = "Deletes a specific course by ID for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = HttpStatuses.NO_CONTENT),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        courseService.deleteCourse(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
