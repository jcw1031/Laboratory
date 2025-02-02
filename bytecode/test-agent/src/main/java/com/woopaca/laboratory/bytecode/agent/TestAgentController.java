package com.woopaca.laboratory.bytecode.agent;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

@Slf4j
public class TestAgentController {

    private static ResettableClassFileTransformer transformer;

    public static void installAdvice(Instrumentation instrumentation) {
        transformer = new AgentBuilder.Default()
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.nameStartsWith("com.woopaca.laboratory.bytecode"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(TestAdvice.class).on(ElementMatchers.isMethod())))
                .installOn(instrumentation);
    }

    public static void uninstallAdvice(Instrumentation instrumentation) {
        if (transformer != null) {
            instrumentation.removeTransformer(transformer);
            boolean reset = transformer.reset(instrumentation, RedefinitionStrategy.RETRANSFORMATION);
            log.info("바이트코드 변경 취소 완료: {}", reset);
        }
    }
}
