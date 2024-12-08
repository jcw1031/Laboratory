package com.woopaca.laboratory.nio.eventloop;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionAcceptHandler implements Handler {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public ConnectionAcceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    @Override
    public void handle() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                ToUpperCaseHandler toUpperCaseHandler = new ToUpperCaseHandler(selector, socketChannel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
