package com.woopaca.laboratory.webflux.netty.operation;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ConnectOperationProcessor implements OperationProcessor {

    @Override
    public void process(SelectionKey selectionKey) throws Exception {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        Selector selector = selectionKey.selector();
        if (channel.finishConnect()) {
            channel.register(selector, SelectionKey.OP_WRITE, selectionKey.attachment());
        } else {
            channel.close();
        }
    }
}
