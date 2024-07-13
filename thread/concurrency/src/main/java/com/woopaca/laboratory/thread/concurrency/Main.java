package com.woopaca.laboratory.thread.concurrency;

public class Main {

    private static int number;
    private static boolean ready;

    public static void main(String[] args) {
        new Reader().start();
        number = 42;
        ready = true;
    }

    static class Reader extends Thread {

        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.println(number);
        }
    }
}