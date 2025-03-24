package com.woopaca.laboratory.redis;

import java.io.Serializable;

public class SerializeTarget implements Serializable {

    private String value;
    private int number;

    public SerializeTarget(String value, int number) {
        this.value = value;
        this.number = number;
    }

    public String getValue() {
        System.out.println("value = " + value);
        return value;
    }

    public int getNumber() {
        System.out.println("value = " + value);
        return number;
    }
}
