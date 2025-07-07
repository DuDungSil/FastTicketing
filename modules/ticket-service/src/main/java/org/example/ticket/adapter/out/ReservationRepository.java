package org.example.ticket.adapter.out;

import java.time.LocalDateTime;
import java.util.List;

import org.example.ticket.domain.entity.Reservation;
import org.example.ticket.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 최적화 필요
    List<Reservation> findAllByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime time);

    // 유저 티켓 예매 개수 집계
    @Query("""
        SELECT COUNT(t)
        FROM Reservation r
        JOIN r.tickets t
        WHERE r.userId = :userId
          AND t.ticketOpen.id = :ticketOpenId
          AND r.status IN ('REQUESTED', 'COMPLETED')
    """)
    int countReservedByUserIdAndTicketOpenId(@Param("userId") Long userId,
            @Param("ticketOpenId") Integer ticketOpenId);

}
