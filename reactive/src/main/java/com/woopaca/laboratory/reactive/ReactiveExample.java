package com.woopaca.laboratory.reactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class ReactiveExample {

    public static void main(String[] args) {
        Flux<String> publisher = Flux.just("Hello", "!", "React", "Programming");
        publisher.subscribe(
                data -> log.info("받은 데이터: {}", data),
                error -> log.error("에러", error),
                () -> log.info("완료!")
        );

        Flux<Integer> numbers = Flux.range(1, 5);
        numbers.map(n -> n * 2)
                .filter(n -> n > 5)
                .map(Object::toString)
                .subscribe(log::info);
    }
}
