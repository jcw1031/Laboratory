package com.woopaca.laboratory.playground.cglib;

public class PersonService {

    public String sayHello(String name) {
        return "Hello " + name;
    }

    public Integer lengthOfName(String name) {
        return name.length();
    }

}
