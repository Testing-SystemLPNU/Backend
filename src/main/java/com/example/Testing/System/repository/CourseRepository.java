package com.example.Testing.System.repository;

import com.example.Testing.System.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
}