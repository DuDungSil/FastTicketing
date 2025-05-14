package org.example.ticket.adapter.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.ticket.domain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByTicketOpenId(Integer ticketOpenId);

    Optional<Ticket> findByTicketOpenIdAndSeatCode(Integer ticketOpenId, String seatCode);

    int countByUserIdAndTicketOpenId(Long userId, Integer ticketOpenId);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'PENDING' AND t.pendingAt <= :cutoff")
    List<Ticket> findPendingOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'HELD' AND t.heldAt <= :cutoff")
    List<Ticket> findOverdueHeldTickets(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'CANCELED' AND t.reservedAt <= :cutoff")
    List<Ticket> findCanceledBefore(@Param("cutoff") LocalDateTime cutoff);

}
