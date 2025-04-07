package com.woopaca.laboratory.kafka;

import org.junit.jupiter.api.Test;

public class ByteToString {

    @Test
    void name() {
        byte[] bytes = {73, 110, 116, 101, 108, 108, 105, 106, 75, 97, 102, 107, 97, 80, 108, 117, 103, 105, 110};
        String s = new String(bytes);
        System.out.println(s);
    }
}
