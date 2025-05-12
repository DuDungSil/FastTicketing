package org.example.ticket.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.example.ticket.domain.enums.OpenType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class TicketOpen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK
    private Integer scheduleId;

    private LocalDateTime openAt;

    // 인당 구매 제한
    private Integer limitPerUser;

    @Enumerated(EnumType.STRING)
    private OpenType openType;

    @OneToMany(mappedBy = "ticketOpen", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    // ==== 비즈니스 로직 ====
    // 생성
    public TicketOpen(Integer scheduleId, LocalDateTime openAt, Integer limitPerUser, OpenType openType, List<Ticket> tickets) {
        this.scheduleId = scheduleId;
        this.openAt = openAt;
        this.limitPerUser = limitPerUser;
        this.openType = openType;
        this.tickets = tickets;

        for (Ticket ticket : tickets) {
            ticket.setTicketOpen(this);
        }
    }

}
