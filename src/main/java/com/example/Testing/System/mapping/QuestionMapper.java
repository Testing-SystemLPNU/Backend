package com.example.Testing.System.mapping;

import com.example.Testing.System.dto.question.QuestionRequestDto;
import com.example.Testing.System.dto.question.QuestionResponseDto;
import com.example.Testing.System.model.Question;

public class QuestionMapper {

    public static Question fromRequestDto(QuestionRequestDto dto) {
        Question question = new Question();
        question.setQuestionText(dto.getQuestionText());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectOption(dto.getCorrectOption());
        return question;
    }

    public static QuestionResponseDto toResponseDto(Question question) {
        QuestionResponseDto dto = new QuestionResponseDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCorrectOption(question.getCorrectOption());
        dto.setCreatedAt(question.getCreatedAt());
        return dto;
    }
}
