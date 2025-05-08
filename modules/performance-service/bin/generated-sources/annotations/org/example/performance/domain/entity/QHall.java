package org.example.performance.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHall is a Querydsl query type for Hall
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHall extends EntityPathBase<Hall> {

    private static final long serialVersionUID = 756972326L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHall hall = new QHall("hall");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final ListPath<Seat, QSeat> seats = this.<Seat, QSeat>createList("seats", Seat.class, QSeat.class, PathInits.DIRECT2);

    public final QVenue venue;

    public QHall(String variable) {
        this(Hall.class, forVariable(variable), INITS);
    }

    public QHall(Path<? extends Hall> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHall(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHall(PathMetadata metadata, PathInits inits) {
        this(Hall.class, metadata, inits);
    }

    public QHall(Class<? extends Hall> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.venue = inits.isInitialized("venue") ? new QVenue(forProperty("venue")) : null;
    }

}

