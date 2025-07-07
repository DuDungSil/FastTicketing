package org.example.ticket.adapter.out;

import java.util.List;

import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByTicketOpenId(Integer ticketOpenId);

    // 최적화 필요
    List<Ticket> findByStatus(TicketStatus status);

}
