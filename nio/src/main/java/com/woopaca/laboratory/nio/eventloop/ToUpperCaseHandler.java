package com.woopaca.laboratory.nio.eventloop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ToUpperCaseHandler implements Handler {

    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(256);

    public ToUpperCaseHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);

        this.selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        this.selectionKey.attach(this);
    }

    @Override
    public void handle() {
        try {
            if (selectionKey.isReadable()) {
                handleReadEvent();
            }
            if (selectionKey.isWritable()) {
                handleWriteEvent();
            }
        } catch (IOException e) {
            closeSocket(socketChannel);
            throw new RuntimeException(e);
        }
    }

    private void handleReadEvent() throws IOException {
        int data = socketChannel.read(byteBuffer);

        if (data == -1) {
            closeSocket(socketChannel);
        }

        if (data > 0) {
            byteBuffer.flip();
            toUpperCase(byteBuffer);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void handleWriteEvent() throws IOException {
        socketChannel.write(byteBuffer);

        while (!byteBuffer.hasRemaining()) {
            byteBuffer.compact();
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    private static void closeSocket(SocketChannel socket) {
        try {
            socket.close();
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
