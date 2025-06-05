package com.woopaca.laboratory.webflux.custom;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class WoopacaClient {

    private final SocketChannel socketChannel;

    public WoopacaClient() throws IOException {
        this.socketChannel = SocketChannel.open();
    }

    public WoopacaClient(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public Mono<String> get(String url) {
        return Mono.create(sink -> {
            try {
                URI uri = URI.create(url);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 80 : uri.getPort();
                String path = uri.getRawPath().isEmpty() ? "/" : uri.getRawPath();

                SocketChannel channel = SocketChannel.open();
                channel.configureBlocking(false);

                Selector selector = Selector.open();
                channel.register(selector, SelectionKey.OP_CONNECT);
                channel.connect(new InetSocketAddress(host, port));

                new Thread(() -> {
                    try {
                        boolean requestSent = false;
                        StringBuilder responseBuilder = new StringBuilder();

                        while (true) {
                            selector.select();
                            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                            while (iter.hasNext()) {
                                SelectionKey key = iter.next();
                                iter.remove();

                                if (key.isConnectable()) {
                                    SocketChannel sc = (SocketChannel) key.channel();
                                    if (sc.finishConnect()) {
                                        sc.register(selector, SelectionKey.OP_WRITE);
                                    }
                                } else if (key.isWritable() && !requestSent) {
                                    SocketChannel sc = (SocketChannel) key.channel();
                                    String request = "GET " + path + " HTTP/1.1\r\n" +
                                                     "Host: " + host + "\r\n" +
                                                     "Connection: close\r\n\r\n";
                                    sc.write(ByteBuffer.wrap(request.getBytes()));
                                    sc.register(selector, SelectionKey.OP_READ);
                                    requestSent = true;
                                } else if (key.isReadable()) {
                                    SocketChannel sc = (SocketChannel) key.channel();
                                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                                    int read = sc.read(buffer);
                                    if (read == -1) {
                                        sc.close();
                                        selector.close();
                                        sink.success(responseBuilder.toString());
                                        return;
                                    }
                                    buffer.flip();
                                    byte[] bytes = new byte[buffer.remaining()];
                                    buffer.get(bytes);
                                    responseBuilder.append(new String(bytes));
                                }
                            }
                        }
                    } catch (IOException e) {
                        sink.error(e);
                    }
                }).start();

            } catch (Exception e) {
                sink.error(e);
            }
        });
    }
}
