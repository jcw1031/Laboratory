package com.woopaca.laboratory.webflux.netty;

import com.woopaca.laboratory.webflux.netty.operation.ConnectOperationProcessor;
import com.woopaca.laboratory.webflux.netty.operation.OperationProcessor;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class Operations {

    private final Map<Predicate<SelectionKey>, OperationProcessor> operationProcessors;

    public Operations() {
        this.operationProcessors = Map.of(
                SelectionKey::isConnectable, new ConnectOperationProcessor(),
                SelectionKey::isReadable, new ConnectOperationProcessor(),
                SelectionKey::isWritable, new ConnectOperationProcessor()
        );
    }

    public void operate(SelectionKey selectionKey) {
        for (Entry<Predicate<SelectionKey>, OperationProcessor> entry : operationProcessors.entrySet()) {
            if (entry.getKey().test(selectionKey)) {
                try {
                    entry.getValue().process(selectionKey);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
    }
}
