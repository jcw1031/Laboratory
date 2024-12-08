package com.woopaca.laboratory.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Best practice
@Slf4j
public class NioMultiplexingSocketApplication {

    private static final Map<SocketChannel, ByteBuffer> socketChannels = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.bind(new InetSocketAddress(8080));
            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    try {
                        if (selectionKey.isValid()) {
                            if (selectionKey.isAcceptable()) {
                                handleAcceptEvent(selectionKey);
                            }
                            if (selectionKey.isReadable()) {
                                handleReadEvent(selectionKey);
                            }
                            if (selectionKey.isWritable()) {
                                handleWriteEvent(selectionKey);
                            }
                        }
                    } catch (ClosedChannelException e) {
                        closeSocket((SocketChannel) selectionKey.channel());
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    iterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleAcceptEvent(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel == null) {
            return;
        }

        socketChannel.configureBlocking(false);
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
        socketChannels.put(socketChannel, ByteBuffer.allocateDirect(8));
    }

    private static void handleReadEvent(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = socketChannels.get(socketChannel);

        int data = socketChannel.read(byteBuffer);
        if (data == -1) {
            closeSocket(socketChannel);
            socketChannels.remove(socketChannel);
        }

        byteBuffer.flip();
        toUpperCase(byteBuffer);
        socketChannel.configureBlocking(false);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void handleWriteEvent(SelectionKey selectionKey) throws IOException {
        SocketChannel socket = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = socketChannels.get(socket);

        socket.write(byteBuffer);

        while (!byteBuffer.hasRemaining()) {
            byteBuffer.compact();
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    private static void closeSocket(SocketChannel socketChannel) {
        try {
            socketChannel.close();
            socketChannels.remove(socketChannel);
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
