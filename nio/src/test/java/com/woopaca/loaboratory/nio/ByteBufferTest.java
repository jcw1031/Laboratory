package com.woopaca.loaboratory.nio;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ByteBufferTest {

    @Test
    void byteBufferTest() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);
        assertThat(byteBuffer.position()).isEqualTo(0);
        assertThat(byteBuffer.limit()).isEqualTo(8);

        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2);
        byteBuffer.put((byte) 3);
        byteBuffer.put((byte) 4);
        assertThat(byteBuffer.position()).isEqualTo(4);
        assertThat(byteBuffer.limit()).isEqualTo(8);

        byte get = byteBuffer.get(7);
        log.info("get: {}", get);

        byteBuffer.flip();
        assertThat(byteBuffer.position()).isEqualTo(0);
        assertThat(byteBuffer.limit()).isEqualTo(4);

        get = byteBuffer.get(3);
        log.info("get: {}", get);
        assertThat(byteBuffer.position()).isEqualTo(0);

        get = byteBuffer.get();
        log.info("get: {}", get);
        assertThat(byteBuffer.position()).isEqualTo(1);
    }
}
