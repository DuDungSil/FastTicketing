package org.example.ticket.application.dto;

public record QueueStatusDto(
        long position,
        boolean canEnter
        ) {

}
