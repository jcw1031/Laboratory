package com.woopaca.laboratory.statemachine;

import lombok.Getter;

@Getter
public enum EventType {

    ;

    private final String name;

    EventType(String name) {
        this.name = name;
    }
}
