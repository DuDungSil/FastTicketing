package org.example.ticket.application;

import java.util.List;
import java.util.stream.Collectors;

import org.example.performance.domain.entity.Seat;
import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.application.dto.TicketStatusDto;
import org.example.ticket.domain.entity.Ticket;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    // 티켓 생성 ( 좌석 정보 이용 )
    public List<Ticket> generateTickets(List<Seat> seats) {
        List<Ticket> newTickets = seats.stream().map(seat -> new Ticket(seat.getSeatCode()))
                .collect(Collectors.toList());

        return newTickets;
    }

    // 티켓 전체 현황
    public List<TicketStatusDto> getTicketStatus(Integer ticketOpenId) {
        List<Ticket> tickets = ticketRepository.findAllByTicketOpenId(ticketOpenId);

        return tickets.stream()
                .map(ticket -> new TicketStatusDto(ticket.getId(), ticket.getSeatCode(), ticket.getStatus()))
                .collect(Collectors.toList());
    }

    // 티켓 예매
    @Transactional
    public void reserveTickets(Long userId, List<Long> ticketIds) {
        // Rock
        List<Ticket> tickets = ticketRepository.findAllByIdWithLock(ticketIds);

        // 예매 1사람당 제한 확인??
        // 예매 처리
        for (Ticket ticket : tickets) {
            ticket.reserve(userId);
        }

        // 성공 시 웹소켓 브로드캐스트?
    }

    // 티켓 결제
    @Transactional
    public void payTickets(Long userId, List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        // 실제 결제 로직?
        // 결제 처리
        for (Ticket ticket : tickets) {
            ticket.pay(userId);
        }
    }

    // 결제 미 완료 시 취소 ( PENDING 상태에서 )
    @Transactional
    public void cancelPendingTickets(Long userId, List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        for (Ticket ticket : tickets) {
            ticket.cancelPending(userId);
        }
    }

    // 결제 완료 시 취소 ( RESERVED 상태에서 )
    @Transactional
    public void cancelReservedTickets(Long userId, List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        // 환불?
        for (Ticket ticket : tickets) {
            ticket.cancelReserved(userId);
        }
    }

}
