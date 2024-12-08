package com.woopaca.laboratory.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Bad Practice of NIO: non-blocking polling
public class NioNonBlockingSocketApplication {

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(8080));
            serverSocketChannel.configureBlocking(false);
            Map<SocketChannel, ByteBuffer> socketChannels = new ConcurrentHashMap<>();
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    socketChannel.configureBlocking(false);
                    socketChannels.put(socketChannel, ByteBuffer.allocateDirect(64));
                }

                socketChannels.keySet()
                        .removeIf(channel -> !channel.isOpen());
                socketChannels.forEach((channel, byteBuffer) -> {
                    try {
                        int data = channel.read(byteBuffer);
                        if (data == -1) {
                            closeSocket(channel);
                            return;
                        }

                        if (data != 0) {
                            byteBuffer.flip();
                            toUpperCase(byteBuffer);
                            while (byteBuffer.hasRemaining()) {
                                channel.write(byteBuffer);
                            }

                            byteBuffer.compact();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void closeSocket(SocketChannel socketChannel) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleRequest(SocketChannel socketChannel) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(64);
        try {
            while (socketChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                toUpperCase(byteBuffer);

                while (byteBuffer.hasRemaining()) {
                    socketChannel.write(byteBuffer);
                }

                byteBuffer.compact();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void toUpperCase(final ByteBuffer byteBuffer) {
        for (int x = 0; x < byteBuffer.limit(); x++) {
            byteBuffer.put(x, (byte) toUpperCase(byteBuffer.get(x)));
        }
    }

    private static int toUpperCase(int data) {
        return Character.isLetter(data) ? Character.toUpperCase(data) : data;
    }
}
