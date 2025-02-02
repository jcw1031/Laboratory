package com.woopaca.laboratory.bytecode.application;

public class TestApplication {

    public static void main(String[] args) throws InterruptedException {
        TestTarget target = new TestTarget();
        while (true) {
            Thread.sleep(1_000);
            target.test();
        }
    }
}
