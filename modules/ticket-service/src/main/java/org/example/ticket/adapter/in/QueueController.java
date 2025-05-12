package org.example.ticket.adapter.in;

import java.util.UUID;

import org.example.ticket.application.QueueService;
import org.example.ticket.application.dto.QueueStatusDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/queue")
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/enter")
    public ResponseEntity<String> enter(@RequestParam UUID queueToken, @RequestParam Integer ticketOpenId) {
        queueService.enterQueue(queueToken, ticketOpenId);
        return ResponseEntity.ok("대기열 진입 성공");
    }

    @GetMapping("/status")
    public ResponseEntity<QueueStatusDto> status(@RequestParam UUID queueToken, @RequestParam Integer ticketOpenId) {
        QueueStatusDto queueStatusDto = queueService.getQueueStatus(queueToken, ticketOpenId);
        return ResponseEntity.ok(queueStatusDto);
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leave(@RequestParam UUID queueToken, @RequestParam Integer ticketOpenId) {
        queueService.leaveQueue(queueToken, ticketOpenId);
        return ResponseEntity.ok("대기열 퇴장 성공");
    }

}
