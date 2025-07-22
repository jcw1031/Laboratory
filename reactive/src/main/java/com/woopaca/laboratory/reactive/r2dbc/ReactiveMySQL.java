package com.woopaca.laboratory.reactive.r2dbc;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.test.StepVerifier;

@Slf4j
@Component
public class ReactiveMySQL {

    private final R2dbcEntityTemplate entityTemplate;

    public ReactiveMySQL(R2dbcEntityTemplate entityTemplate) {
        this.entityTemplate = entityTemplate;
    }

    @PostConstruct
    public void init() {
        entityTemplate.getDatabaseClient()
                .sql("""
                        DROP TABLE IF EXISTS person;
                        
                        CREATE TABLE IF NOT EXISTS person
                        (
                            id   VARCHAR(255) PRIMARY KEY,
                            name VARCHAR(255),
                            age  INT
                        );
                        """)
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        entityTemplate.insert(Person.class)
                .using(new Person("joe", "Joe", 34))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        entityTemplate.select(Person.class)
                .first()
                .doOnNext(it -> log.info("{}", it))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}
