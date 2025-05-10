package com.example.Testing.System.repository;

import com.example.Testing.System.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByFullNameAndGroupName(String fullName, String groupName);
    @Query("SELECT s FROM Student s WHERE LOWER(s.groupName) = LOWER(:groupName)")
    List<Student> findAllByGroupNameIgnoreCase(@Param("groupName") String groupName);
}