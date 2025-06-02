package com.woopaca.laboratory.webflux;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class EmployeeRepository {


    public Mono<Employee> findById(String id) {
        return Mono.just(new Employee("Employee " + id))
                .delayElement(Duration.ofMillis(20));
    }

    public Flux<Employee> findAll() {
        return Flux.range(1, 10)
                .map(i -> new Employee("Employee " + i))
                .delayElements(Duration.ofMillis(12));
    }
}
