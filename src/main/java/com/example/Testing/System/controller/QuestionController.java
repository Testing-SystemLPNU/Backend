package com.example.Testing.System.controller;

import com.example.Testing.System.dto.question.QuestionRequestDto;
import com.example.Testing.System.dto.question.QuestionResponseDto;
import com.example.Testing.System.mapping.QuestionMapper;
import com.example.Testing.System.model.Question;
import com.example.Testing.System.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses/{courseId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Question question = questionService.getQuestionById(id, userDetails.getUsername());
        return ResponseEntity.ok(QuestionMapper.toResponseDto(question));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        questionService.deleteQuestion(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
