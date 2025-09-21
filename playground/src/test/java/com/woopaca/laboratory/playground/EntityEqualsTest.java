package com.woopaca.laboratory.playground;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class EntityEqualsTest {

    @Test
    void equals() {
        TestSubEntity entity1 = new TestSubEntity(1L);
        TestSubEntity2 entity2 = new TestSubEntity2(1L);

        Assertions.assertThat(entity1.equals(entity2)).isFalse();
    }
}
