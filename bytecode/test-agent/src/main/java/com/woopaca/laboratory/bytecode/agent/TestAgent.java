package com.woopaca.laboratory.bytecode.agent;

import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TestAgent {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        log.info("TestAgent.premain");
        TestAgentController.installAdvice(instrumentation);

        Scanner scanner = new Scanner(System.in);
        executorService.execute(() -> {
            while (true) {
                String input = scanner.next();
                if (Objects.equals(input, "off")) {
                    log.info("제거");
                    TestAgentController.uninstallAdvice(instrumentation);
                    continue;
                }
                if (Objects.equals(input, "on")) {
                    log.info("설치");
                    TestAgentController.installAdvice(instrumentation);
                }
            }
        });
    }
}
