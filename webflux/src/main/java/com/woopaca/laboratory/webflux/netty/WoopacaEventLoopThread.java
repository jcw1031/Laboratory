package com.woopaca.laboratory.webflux.netty;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WoopacaEventLoopThread extends Thread {

    private final Selector selector;
    private final Queue<Runnable> taskQueue;
    private final Operations operations;

    public WoopacaEventLoopThread() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.taskQueue = new ConcurrentLinkedQueue<>();
        this.operations = new Operations();

        super.setName("woopaca-");
    }

    public void registerTask(Runnable task) {
        taskQueue.offer(task);
        selector.wakeup();
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();

                while (!taskQueue.isEmpty()) {
                    taskQueue.poll().run();
                }

                selector.selectedKeys()
                        .forEach(operations::operate);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
