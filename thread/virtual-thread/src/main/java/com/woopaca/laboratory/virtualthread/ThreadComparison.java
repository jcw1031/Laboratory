package com.woopaca.laboratory.virtualthread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadComparison {

    private static final int TASK_COUNT = 10000;
    private static final int SLEEP_MILLIS = 100;

    public static void main(String[] args) throws Exception {
        compareThreads("Platform Threads", Executors.newFixedThreadPool(200));
        compareThreads("Virtual Threads", Executors.newVirtualThreadPerTaskExecutor());
    }

    private static void compareThreads(String threadType, ExecutorService executor) throws Exception {
        AtomicInteger activeTaskCount = new AtomicInteger(0);
        AtomicInteger maxActiveTasks = new AtomicInteger(0);

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        int initialThreadCount = threadMXBean.getThreadCount();

        long startTime = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(TASK_COUNT);
        for (int i = 0; i < TASK_COUNT; i++) {
            executor.submit(() -> {
                int current = activeTaskCount.incrementAndGet();
                maxActiveTasks.updateAndGet(max -> Math.max(max, current));
                try {
                    Thread.sleep(SLEEP_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    activeTaskCount.decrementAndGet();
                    latch.countDown();
                }
            });
        }

        // 최대 플랫폼 스레드 수를 주기적으로 체크
        AtomicInteger maxPlatformThreads = new AtomicInteger(initialThreadCount);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            int currentThreadCount = threadMXBean.getThreadCount();
            maxPlatformThreads.updateAndGet(max -> Math.max(max, currentThreadCount));
        }, 0, 10, TimeUnit.MILLISECONDS);

        latch.await();
        long endTime = System.currentTimeMillis();

        scheduler.shutdown();
        executor.shutdown();

        System.out.println(threadType + " results:");
        System.out.println("Max active tasks: " + maxActiveTasks.get());
        System.out.println("Max platform threads: " + maxPlatformThreads.get());
        System.out.println("Initial platform threads: " + initialThreadCount);
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
        System.out.println();
    }
}