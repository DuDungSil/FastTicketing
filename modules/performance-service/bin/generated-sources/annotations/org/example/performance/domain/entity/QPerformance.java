package org.example.performance.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformance is a Querydsl query type for Performance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformance extends EntityPathBase<Performance> {

    private static final long serialVersionUID = 1279697059L;

    public static final QPerformance performance = new QPerformance("performance");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<PerformanceSchedule, QPerformanceSchedule> schedules = this.<PerformanceSchedule, QPerformanceSchedule>createList("schedules", PerformanceSchedule.class, QPerformanceSchedule.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public QPerformance(String variable) {
        super(Performance.class, forVariable(variable));
    }

    public QPerformance(Path<? extends Performance> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPerformance(PathMetadata metadata) {
        super(Performance.class, metadata);
    }

}

