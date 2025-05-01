package com.example.Testing.System.repository;

import com.example.Testing.System.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}