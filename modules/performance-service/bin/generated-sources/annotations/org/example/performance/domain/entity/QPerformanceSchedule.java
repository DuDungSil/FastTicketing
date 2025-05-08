package org.example.performance.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformanceSchedule is a Querydsl query type for PerformanceSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformanceSchedule extends EntityPathBase<PerformanceSchedule> {

    private static final long serialVersionUID = -1384496038L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerformanceSchedule performanceSchedule = new QPerformanceSchedule("performanceSchedule");

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final QHall hall;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QPerformance performance;

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public QPerformanceSchedule(String variable) {
        this(PerformanceSchedule.class, forVariable(variable), INITS);
    }

    public QPerformanceSchedule(Path<? extends PerformanceSchedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerformanceSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerformanceSchedule(PathMetadata metadata, PathInits inits) {
        this(PerformanceSchedule.class, metadata, inits);
    }

    public QPerformanceSchedule(Class<? extends PerformanceSchedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hall = inits.isInitialized("hall") ? new QHall(forProperty("hall"), inits.get("hall")) : null;
        this.performance = inits.isInitialized("performance") ? new QPerformance(forProperty("performance")) : null;
    }

}

