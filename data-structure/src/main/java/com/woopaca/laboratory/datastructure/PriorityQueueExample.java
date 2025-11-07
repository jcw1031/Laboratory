package com.woopaca.laboratory.datastructure;

import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

@Slf4j
public class PriorityQueueExample {

    public static void main(String[] args) {
        Queue<Data> queue = new PriorityQueue<>(Comparator.comparingInt(Data::number));
        queue.offer(new Data(5, "five"));
        queue.offer(new Data(2, "two"));
        queue.offer(new Data(3, "three"));

        Data first = queue.poll();
        log.info("first: {}", first);

    }

    record Data(Integer number, String value) {
    }
}
