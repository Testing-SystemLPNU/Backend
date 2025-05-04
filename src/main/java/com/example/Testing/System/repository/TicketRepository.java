package com.example.Testing.System.repository;

import com.example.Testing.System.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByCourseId(Integer courseId);
    Optional<Ticket> findById(Integer id);
}