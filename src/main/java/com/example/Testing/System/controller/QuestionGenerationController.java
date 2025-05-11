package com.example.Testing.System.controller;

import com.example.Testing.System.dto.question.QuestionDto;
import com.example.Testing.System.model.Question;
import com.example.Testing.System.service.impl.AIQuestionServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
public class QuestionGenerationController {

    private final AIQuestionServiceImpl aiQuestionService;

    @PostMapping(value = "/{courseId}/questions/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<QuestionDto>> generateQuestions(
            @RequestParam("file") MultipartFile file,
            @PathVariable Integer courseId
    ) throws IOException {

        List<Question> saved = aiQuestionService.generateAndSaveQuestions(file, courseId);
        List<QuestionDto> result = saved.stream().map(q -> new QuestionDto(
                q.getQuestionText(),
                q.getOptionA(),
                q.getOptionB(),
                q.getOptionC(),
                q.getOptionD(),
                q.getCorrectOption()
        )).toList();
        return ResponseEntity.ok(result);
    }
}