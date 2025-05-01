package com.example.Testing.System.repository;

import com.example.Testing.System.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByUserId(Integer userId);
}