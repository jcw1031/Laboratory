package com.woopaca.laboratory.statemachine;

import lombok.Getter;

@Getter
public enum StateType {

    ;

    private final String name;

    StateType(String name) {
        this.name = name;
    }
}
