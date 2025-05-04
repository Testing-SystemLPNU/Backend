package com.example.Testing.System.repository;

import com.example.Testing.System.model.Ticketquestion;
import com.example.Testing.System.model.TicketquestionId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketQuestionRepository extends JpaRepository<Ticketquestion, TicketquestionId> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Ticketquestion tq WHERE tq.ticket.id = :ticketId")
    void deleteByTicketId(@Param("ticketId") Long ticketId);
    List<Ticketquestion> findByTicketId(Integer ticketId);
}