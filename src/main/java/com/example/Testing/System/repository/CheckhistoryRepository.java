package com.example.Testing.System.repository;

import com.example.Testing.System.model.Checkhistory;
import com.example.Testing.System.model.Student;
import com.example.Testing.System.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckhistoryRepository extends JpaRepository<Checkhistory, Integer> {
    Optional<Checkhistory> findTopByTicketOrderByCreatedAtDesc(Ticket ticket);
}