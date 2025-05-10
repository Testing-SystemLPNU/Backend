package com.example.Testing.System.repository;

import com.example.Testing.System.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByFullNameAndGroupName(String fullName, String groupName);
}