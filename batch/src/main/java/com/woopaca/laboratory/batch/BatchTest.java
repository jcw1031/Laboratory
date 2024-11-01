package com.woopaca.laboratory.batch;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Component;

@Component
public class BatchTest {

    private final EntityManagerFactory entityManagerFactory;

    public BatchTest(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
}
