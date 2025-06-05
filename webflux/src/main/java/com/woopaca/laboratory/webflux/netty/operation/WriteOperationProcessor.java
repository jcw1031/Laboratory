package com.woopaca.laboratory.webflux.netty.operation;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class WriteOperationProcessor implements OperationProcessor {

    @Override
    public void process(SelectionKey selectionKey) throws Exception {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        Selector selector = selectionKey.selector();

        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        int bytesWritten = channel.write(buffer);
        if (bytesWritten > 0) {
            if (!buffer.hasRemaining()) {
                buffer.clear();
                selectionKey.interestOps(SelectionKey.OP_READ);
            }
        } else {
            channel.close();
        }
    }
}
