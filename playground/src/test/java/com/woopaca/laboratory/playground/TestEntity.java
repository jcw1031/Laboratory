package com.woopaca.laboratory.playground;

import java.io.Serializable;
import java.util.Objects;

public class TestEntity implements Serializable {

    private Long id;

    protected TestEntity(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TestEntity that = (TestEntity) o;
        return Objects.equals(id, that.id);
    }
}
