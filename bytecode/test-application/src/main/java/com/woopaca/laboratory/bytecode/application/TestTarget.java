package com.woopaca.laboratory.bytecode.application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTarget {

    private int count = 0;

    public void test() {
        log.info("count: {}", ++count);
    }
}
