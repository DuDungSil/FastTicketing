package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;

import org.example.performance.adapter.out.PerformanceScheduleRepository;
import org.example.performance.domain.entity.PerformanceSchedule;
import org.example.performance.domain.entity.Seat;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.OpenType;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TicketOpenService {

    private final TicketService ticketService;
    private final TicketOpenRepository ticketOpenRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;

    // 티켓 예매 정책 만들기
    @Transactional
    public void createTicketOpen(Integer scheduleId, LocalDateTime openAt, Integer limitPerUser, OpenType openType) {
        // 스케줄 정보 가져오기
        PerformanceSchedule schedule = performanceScheduleRepository.findById(scheduleId).orElseThrow();

        // 좌석 정보 가져오기
        List<Seat> seats = schedule.getHall().getSeats();

        // 티켓 생성
        List<Ticket> newTickets = ticketService.generateTickets(seats);

        // 티켓 오픈 생성
        TicketOpen newTicketOpen = new TicketOpen(scheduleId, openAt, limitPerUser, openType, newTickets);

        // 티켓 오픈 저장
        ticketOpenRepository.save(newTicketOpen);
    }

}
