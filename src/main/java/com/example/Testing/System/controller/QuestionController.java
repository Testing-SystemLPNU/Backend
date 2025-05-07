package com.example.Testing.System.controller;

import com.example.Testing.System.constant.HttpStatuses;
import com.example.Testing.System.dto.question.QuestionRequestDto;
import com.example.Testing.System.dto.question.QuestionResponseDto;
import com.example.Testing.System.mapping.QuestionMapper;
import com.example.Testing.System.model.Question;
import com.example.Testing.System.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/courses/{courseId}/questions")
@Tag(name = "Questions", description = "Endpoints for managing questions within a course")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(summary = "Create question", description = "Create a new question associated with a specific course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = QuestionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestionForCourse(
            @PathVariable Integer courseId,
            @RequestBody QuestionRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Question question = QuestionMapper.fromRequestDto(requestDto);
        Question saved = questionService.createQuestionForCourse(courseId, question, userDetails.getUsername());
        return ResponseEntity.ok(QuestionMapper.toResponseDto(saved));
    }

    @Operation(summary = "Get all questions by course", description = "Retrieve all questions related to a specific course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestionResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByCourse(
            @PathVariable Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<QuestionResponseDto> questions = questionService.getQuestionsByCourseId(courseId, userDetails.getUsername())
                .stream()
                .map(QuestionMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Get question by ID", description = "Retrieve a specific question by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = QuestionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Question question = questionService.getQuestionById(id, userDetails.getUsername());
        return ResponseEntity.ok(QuestionMapper.toResponseDto(question));
    }

    @Operation(summary = "Update question", description = "Update a question by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = QuestionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable Integer id,
            @RequestBody QuestionRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Question updatedQuestion = QuestionMapper.fromRequestDto(requestDto);
        Question updated = questionService.updateQuestion(id, updatedQuestion, userDetails.getUsername());
        return ResponseEntity.ok(QuestionMapper.toResponseDto(updated));
    }

    @Operation(summary = "Delete question", description = "Delete a specific question by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = HttpStatuses.NO_CONTENT),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        questionService.deleteQuestion(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
