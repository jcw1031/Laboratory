package com.woopaca.laboratory.transaction.deadlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionDeadlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionDeadlockApplication.class, args);
    }
}
