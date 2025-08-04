package com.woopaca.laboratory.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MultiThreadNIOEchoServer {

    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 8192;
    private static final int WORKER_THREADS = Runtime.getRuntime().availableProcessors();

    private ServerSocketChannel serverChannel;
    private Selector acceptSelector;
    private WorkerThread[] workers;
    private AtomicLong connectionCounter = new AtomicLong(0);

    public static void main(String[] args) {
        try {
            new MultiThreadNIOEchoServer().start();
        } catch (IOException ignored) {
        }
    }

    private static class WorkerThread extends Thread {

        private final Selector selector;
        private final Map<SocketChannel, ByteBuffer> clientBuffers = new ConcurrentHashMap<>();
        private final Queue<SocketChannel> newConnections = new ConcurrentLinkedQueue<>();

        private volatile boolean running = true;

        public WorkerThread(String name) throws IOException {
            super(name);
            this.selector = Selector.open();
            setDaemon(true);
        }

        public void addConnection(SocketChannel channel) {
            newConnections.offer(channel);
            selector.wakeup();
        }

        @Override
        public void run() {
            log.info("워커 스레드 시작: {}", getName());

            while (running) {
                try {
                    processNewConnections();

                    selector.select(1_000);
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isReadable()) {
                            handleRead(key);
                        } else if (key.isWritable()) {
                            handleWrite(key);
                        }
                    }
                } catch (IOException e) {
                    log.error("워커 스레드 오류: {} - {}", getName(), e.getMessage());
                }
            }
        }

        private void processNewConnections() throws IOException {
            SocketChannel channel;
            while ((channel = newConnections.poll()) != null) {
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                clientBuffers.put(channel, ByteBuffer.allocate(BUFFER_SIZE));
                log.info("[{}] 새 클라이언트: {}", getName(), channel.getRemoteAddress());
            }
        }

        private void handleRead(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = clientBuffers.get(channel);

            if (buffer == null)
                return;

            buffer.clear();
            int bytesRead = channel.read(buffer);

            if (bytesRead > 0) {
                buffer.flip();

                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                log.info("[{}] 받음: {}", getName(), new String(data).trim());

                buffer.clear();
                buffer.put(data);
                buffer.flip();

                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            } else if (bytesRead == -1) {
                closeChannel(channel);
            }
        }

        private void handleWrite(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = clientBuffers.get(channel);

            if (buffer != null && buffer.hasRemaining()) {
                channel.write(buffer);

                if (!buffer.hasRemaining()) {
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }

        private void closeChannel(SocketChannel channel) {
            try {
                log.info("[{}] 클라이언트 연결 종료: {}", getName(), channel.getRemoteAddress());
                channel.close();
            } catch (IOException ignored) {
            }
            clientBuffers.remove(channel);
        }

        public int getConnectionCount() {
            return clientBuffers.size();
        }

    }

    public void start() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);

        acceptSelector = Selector.open();
        serverChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);

        workers = new WorkerThread[WORKER_THREADS];
        for (int i = 0; i < WORKER_THREADS; i++) {
            workers[i] = new WorkerThread("Worker-" + i);
            workers[i].start();
        }

        log.info("멀티스레드 NIO 에코 서버 시작: {}", PORT);
        log.info("워커 스레드 수: {}", WORKER_THREADS);

        startStatsThread();

        while (true) {
            acceptSelector.select();

            Set<SelectionKey> selectedKeys = acceptSelector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    handleAccept();
                }
            }
        }
    }

    private void handleAccept() throws IOException {
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel != null) {
            int workerIndex = (int) (connectionCounter.getAndIncrement() % WORKER_THREADS);
            workers[workerIndex].addConnection(clientChannel);

            log.info("새 연결을 Worker-{}에 할당", workerIndex);
        }
    }

    private void startStatsThread() {
        Thread statsThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5_000);

                    log.info("=== 서버 통계 ===");
                    int totalConnections = 0;
                    for (int i = 0; i < workers.length; i++) {
                        int connections = workers[i].getConnectionCount();
                        totalConnections += connections;
                        log.info("Worker-{}: {} 연결", i, connections);
                    }
                    log.info("총 연결 수: {}", totalConnections);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        statsThread.setDaemon(true);
        statsThread.start();
    }
}