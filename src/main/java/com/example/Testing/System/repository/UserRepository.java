package com.example.Testing.System.repository;

import com.example.Testing.System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}