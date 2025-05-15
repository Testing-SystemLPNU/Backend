package com.example.Testing.System.service.impl;

import com.example.Testing.System.dto.question.QuestionDto;
import com.example.Testing.System.model.Course;
import com.example.Testing.System.model.Question;
import com.example.Testing.System.repository.CourseRepository;
import com.example.Testing.System.repository.QuestionRepository;
import com.example.Testing.System.service.AIQuestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIQuestionServiceImpl implements AIQuestionService {
    private final QuestionRepository questionRepo;
    private final CourseRepository courseRepo;

    @Override
    public List<Question> generateAndSaveQuestions(MultipartFile file, Integer courseId) throws IOException {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        String content = extractTextFromFile(file);
        String prompt = buildPrompt(content);
        String aiResponse = callOllama(prompt);
        List<QuestionDto> questionDtos = parseResponse(aiResponse);

        List<Question> saved = questionDtos.stream().map(dto -> {
            Question q = new Question();
            q.setCourse(course);
            q.setQuestionText(dto.getQuestionText());
            q.setOptionA(dto.getOptionA());
            q.setOptionB(dto.getOptionB());
            q.setOptionC(dto.getOptionC());
            q.setOptionD(dto.getOptionD());
            q.setCorrectOption(dto.getCorrectOption());
            return q;
        }).toList();

        return questionRepo.saveAll(saved);
    }

    private String extractTextFromFile(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename.endsWith(".txt")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } else if (filename.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                return doc.getParagraphs().stream()
                        .map(XWPFParagraph::getText)
                        .collect(Collectors.joining("\n"));
            }
        }
        throw new IllegalArgumentException("Unsupported file type: " + filename);
    }

    private String buildPrompt(String theory) {
        return """
           Based on the following theory text, generate exactly 10 multiple choice questions.

           Format your output as a **valid JSON array**, where each item follows **this structure**:

           {
             "questionText": "...",
             "optionA": "...",
             "optionB": "...",
             "optionC": "...",
             "optionD": "...",
             "correctOption": "A" // or "B", "C", or "D"
           }

           Do NOT include any explanation or text outside of the JSON array.

           === THEORY TEXT ===
           %s
           """.formatted(theory);
    }

    private String callOllama(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", "llama3.2",
                "prompt", prompt,
                "stream", false
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:11434/api/generate",
                request,
                Map.class
        );
        System.out.println("🧠 AI Response:\n" + response.getBody().get("response"));

        return (String) response.getBody().get("response");
    }

    private List<QuestionDto> parseResponse(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String trimmed = response.trim();

        if (!trimmed.startsWith("[")) {
            trimmed = "[" + trimmed;
        }
        if (!trimmed.endsWith("]")) {
            trimmed = trimmed + "]";
        }

        trimmed = trimmed
                .replaceAll("}\\s*\\{", "}, {")
                .replaceAll(",\\s*]", "]")
                .replaceAll(",\\s*}", "}")
                .replaceAll("\\\\", "");

        try {
            return mapper.readValue(trimmed, new TypeReference<List<QuestionDto>>() {});
        } catch (Exception ex) {
            System.err.println("❌ JSON parse error: " + ex.getMessage());
            throw ex;
        }
    }
}
