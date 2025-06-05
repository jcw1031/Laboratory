package com.woopaca.laboratory.webflux.netty.operation;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface OperationProcessor {

    void process(SelectionKey selectionKey) throws Exception;
}
