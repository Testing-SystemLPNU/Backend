package com.example.Testing.System.service;


import com.example.Testing.System.model.Question;

import java.util.List;


public interface QuestionService {
    Question createQuestionForCourse(Integer courseId, Question question, String userEmail);
    List<Question> getQuestionsByCourseId(Integer courseId, String userEmail);
    Question getQuestionById(Integer id, String userEmail);
    void deleteQuestion(Integer id, String userEmail);
    Question updateQuestion(Integer id, Question updatedQuestion, String userEmail);
}