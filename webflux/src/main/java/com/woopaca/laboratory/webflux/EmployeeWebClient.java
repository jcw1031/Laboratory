package com.woopaca.laboratory.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class EmployeeWebClient {

    private final WebClient webClient;

    public EmployeeWebClient() {
        this.webClient = WebClient.create("http://localhost:8080");
    }

    public void mono() {
        Mono<Employee> employeeMono = webClient.get()
                .uri("/employees/{id}", "1")
                .retrieve()
                .bodyToMono(Employee.class);

        employeeMono.subscribe(this::logging);
    }

    public void flux() {
        Flux<Employee> employeeFlux = webClient.get()
                .uri("/employees")
                .retrieve()
                .bodyToFlux(Employee.class);

        employeeFlux.subscribe(this::logging);
    }

    private void logging(Employee employee) {
        log.info("employee: {}", employee);
    }
}
