package com.woopaca.laboratory.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

// Bad Practice of NIO: blocking
public class NioBlockingSocketApplication {

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(8080));
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                handleRequest(socketChannel);
            }
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
