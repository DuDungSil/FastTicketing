package org.example.ticket.adapter.out;

import java.util.List;
import java.util.Optional;

import org.example.ticket.domain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByTicketOpenId(Integer ticketOpenId);

    Optional<Ticket> findByTicketOpenIdAndSeatCode(Integer ticketOpenId, String seatCode);

    // ROCK 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id IN :ids")
    List<Ticket> findAllByIdWithLock(@Param("ids") List<Long> ids);

}
