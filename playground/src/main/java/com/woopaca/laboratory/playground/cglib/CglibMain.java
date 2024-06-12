package com.woopaca.laboratory.playground.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;

public class CglibMain {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback((FixedValue) () -> "Hello Chanwoo!");

        PersonService proxy = (PersonService) enhancer.create();
        String response = proxy.sayHello(null);
        System.out.println("response = " + response);
    }

}
