package com.woopaca.laboratory.bytecode.agent;

import net.bytebuddy.asm.Advice;

public class TestAdvice {

    @SuppressWarnings("unused")
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin String signature) {
        System.out.println("됐음");
    }
}
