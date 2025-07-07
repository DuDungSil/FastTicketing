package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.performance.adapter.out.PerformanceScheduleRepository;
import org.example.performance.domain.entity.PerformanceSchedule;
import org.example.performance.domain.entity.Seat;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.application.dto.TicketOpenDto;
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
        List<Long> seatIds = seats.stream().map((seat) -> seat.getId()).collect(Collectors.toList());

        // 티켓 생성
        List<Ticket> newTickets = ticketService.generateTickets(seatIds);

        // 티켓 오픈 생성
        TicketOpen newTicketOpen = TicketOpen.create(scheduleId, openAt, limitPerUser, openType, newTickets);

        // 티켓 오픈 저장
        ticketOpenRepository.save(newTicketOpen);
    }

    // 예매 정책 조회
    public List<TicketOpenDto> getAllTicketOpens() {
        List<TicketOpen> opens = ticketOpenRepository.findAll();
        return TicketOpenDto.fromList(opens);
    }

    public void deleteTicketOpen(Integer id) {
        ticketOpenRepository.deleteById(id);
    }

    // 특정 티켓 오픈 조회
    public TicketOpenDto getTicketOpen(Integer ticketOpenId) {
        TicketOpen ticketOpen = ticketOpenRepository.findById(ticketOpenId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓 오픈입니다."));
        return TicketOpenDto.from(ticketOpen);
    }

}
