package com.woopaca.laboratory.bytecode;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

public class InvocationTracker {

    public static void installAdvice(Instrumentation instrumentation) {
        Advice advice = Advice.to(InvocationTracker.class);
        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(nameStartsWith("com.woopaca.laboratory.bytecode"))
                .transform((builder, typeDescription, classLoader, module) ->
                        builder.visit(Advice.to(InvocationTracker.class).on()))
                .installOn(instrumentation);

    }

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin String signature) {
        System.out.println("됐음");
    }
}
