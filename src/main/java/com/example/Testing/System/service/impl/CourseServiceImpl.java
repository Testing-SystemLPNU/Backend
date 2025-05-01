package com.example.Testing.System.service.impl;

import com.example.Testing.System.dto.course.CourseRequestDto;
import com.example.Testing.System.model.Course;
import com.example.Testing.System.model.User;
import com.example.Testing.System.repository.CourseRepository;
import com.example.Testing.System.service.CourseService;
import com.example.Testing.System.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;

    public CourseServiceImpl(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @Override
    public Course createCourse(CourseRequestDto request, String email) {
        User user = userService.findByEmail(email);
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setUser(user);
        return courseRepository.save(course);
    }

    @Override
    public List<Course> getAllCourses(String email) {
        User user = userService.findByEmail(email);
        return courseRepository.findByUserId(user.getId());
    }

    @Override
    public Course getCourseById(Integer id, String email) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        validateOwnership(course, email);
        return course;
    }

    @Override
    public void deleteCourse(Integer id, String email) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        validateOwnership(course, email);
        courseRepository.delete(course);
    }

    @Override
    public Course updateCourse(Integer id, Course updatedCourse, String email) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        validateOwnership(existing, email);

        existing.setTitle(updatedCourse.getTitle());
        existing.setDescription(updatedCourse.getDescription());

        return courseRepository.save(existing);
    }

    private void validateOwnership(Course course, String email) {
        if (!course.getUser().getEmail().equals(email)) {
            throw new SecurityException("Access denied to this course");
        }
    }
}
