package com.woopaca.laboratory.netty;

import io.netty.handler.codec.LineBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

@Slf4j
public class NettyEchoServer {

    public static void main(String[] args) {
        DisposableServer disposableServer = TcpServer.create()
                .port(7031)
                .doOnConnection(connection -> {
                    connection.addHandlerFirst(new LineBasedFrameDecoder(2048));
                })
                .handle((nettyInbound, nettyOutbound) ->
                        nettyInbound.receive()
                                .asString()
                                .doOnNext(line -> log.info("received: {}", line))
                                .flatMap(line -> nettyOutbound.sendString(Mono.just(line + "\n")))
                )
                .bindNow();

        log.info("disposableServer: {}", disposableServer);

        disposableServer.onDispose()
                .block();
    }
}
