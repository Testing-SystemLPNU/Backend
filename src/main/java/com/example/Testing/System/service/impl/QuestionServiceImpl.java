package com.example.Testing.System.service.impl;

import com.example.Testing.System.model.Course;
import com.example.Testing.System.model.Question;
import com.example.Testing.System.model.User;
import com.example.Testing.System.repository.CourseRepository;
import com.example.Testing.System.repository.QuestionRepository;
import com.example.Testing.System.repository.UserRepository;
import com.example.Testing.System.service.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Question createQuestionForCourse(Integer courseId, Question question, String userEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with id " + courseId + " not found."));

        if (!course.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not authorized to add questions to this course.");
        }

        question.setCourse(course);
        question.setCreatedAt(Instant.now());
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getQuestionsByCourseId(Integer courseId, String userEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with id " + courseId + " not found."));

        if (!course.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not authorized to view questions of this course.");
        }

        return questionRepository.findAll().stream()
                .filter(q -> q.getCourse().getId().equals(courseId))
                .collect(Collectors.toList());
    }

    @Override
    public Question getQuestionById(Integer id, String userEmail) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (!question.getCourse().getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not authorized to view this question.");
        }

        return question;
    }

    @Override
    public void deleteQuestion(Integer id, String userEmail) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (!question.getCourse().getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not authorized to delete this question.");
        }

        questionRepository.deleteById(id);
    }

    @Override
    public Question updateQuestion(Integer id, Question updatedQuestion, String userEmail) {
        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (!existing.getCourse().getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not authorized to update this question.");
        }

        existing.setQuestionText(updatedQuestion.getQuestionText());
        existing.setOptionA(updatedQuestion.getOptionA());
        existing.setOptionB(updatedQuestion.getOptionB());
        existing.setOptionC(updatedQuestion.getOptionC());
        existing.setOptionD(updatedQuestion.getOptionD());
        existing.setCorrectOption(updatedQuestion.getCorrectOption());

        return questionRepository.save(existing);
    }
}