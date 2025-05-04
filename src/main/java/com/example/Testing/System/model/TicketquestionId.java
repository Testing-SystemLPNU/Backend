package com.example.Testing.System.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TicketquestionId implements Serializable {
    private static final long serialVersionUID = 6598667319043074404L;
    @Column(name = "ticket_id", nullable = false)
    private Integer ticketId;

    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TicketquestionId entity = (TicketquestionId) o;
        return Objects.equals(this.questionId, entity.questionId) &&
                Objects.equals(this.ticketId, entity.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, ticketId);
    }

}