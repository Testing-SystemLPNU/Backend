package com.example.Testing.System.repository;

import com.example.Testing.System.model.Ticketquestion;
import com.example.Testing.System.model.TicketquestionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketquestionRepository extends JpaRepository<Ticketquestion, TicketquestionId> {
}