package org.example.ticket.adapter.out;

import org.example.ticket.domain.entity.TicketOpen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketOpenRepository extends JpaRepository<TicketOpen, Integer> {

}
