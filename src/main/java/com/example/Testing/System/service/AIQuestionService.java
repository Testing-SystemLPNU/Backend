package com.example.Testing.System.service;

import com.example.Testing.System.model.Question;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AIQuestionService {
    List<Question> generateAndSaveQuestions(MultipartFile file, Integer courseId) throws IOException;
}
