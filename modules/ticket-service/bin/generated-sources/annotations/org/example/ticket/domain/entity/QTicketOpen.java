package org.example.ticket.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTicketOpen is a Querydsl query type for TicketOpen
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTicketOpen extends EntityPathBase<TicketOpen> {

    private static final long serialVersionUID = 709304043L;

    public static final QTicketOpen ticketOpen = new QTicketOpen("ticketOpen");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> limitPerUser = createNumber("limitPerUser", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> openAt = createDateTime("openAt", java.time.LocalDateTime.class);

    public final EnumPath<org.example.ticket.domain.enums.OpenType> openType = createEnum("openType", org.example.ticket.domain.enums.OpenType.class);

    public final NumberPath<Integer> scheduleId = createNumber("scheduleId", Integer.class);

    public final ListPath<Ticket, QTicket> tickets = this.<Ticket, QTicket>createList("tickets", Ticket.class, QTicket.class, PathInits.DIRECT2);

    public QTicketOpen(String variable) {
        super(TicketOpen.class, forVariable(variable));
    }

    public QTicketOpen(Path<? extends TicketOpen> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTicketOpen(PathMetadata metadata) {
        super(TicketOpen.class, metadata);
    }

}

