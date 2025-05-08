package org.example.ticket.adapter.in;

import java.util.List;

import org.example.ticket.adapter.in.request.TicketStatusRequest;
import org.example.ticket.adapter.in.response.TicketStatusResponse;
import org.example.ticket.application.TicketService;
import org.example.ticket.application.dto.TicketStatusDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class TicketSocketController {

    private final TicketService ticketService;

    // 좌석 현황 초기 로딩 요청 처리 (유저 개인에게만 응답)
    @MessageMapping("/seat-status-init")
    @SendToUser("/queue/seat-status-init")
    public TicketStatusResponse initialSeatStatus(TicketStatusRequest request) {
        List<TicketStatusDto> tickets = ticketService.getTicketStatus(request.ticketOpenId());
        return new TicketStatusResponse(tickets);
    }

}
