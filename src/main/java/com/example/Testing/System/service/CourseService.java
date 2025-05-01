package com.example.Testing.System.service;

import com.example.Testing.System.dto.course.CourseRequestDto;
import com.example.Testing.System.model.Course;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


public interface CourseService {
    Course createCourse(CourseRequestDto request, String email);;
    List<Course> getAllCourses(String email);
    Course getCourseById(Integer id, String email);
    void deleteCourse(Integer id, String email);
    Course updateCourse(Integer id, Course updatedCourse, String email);

}
