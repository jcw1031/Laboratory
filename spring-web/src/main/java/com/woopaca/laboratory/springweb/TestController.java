package com.woopaca.laboratory.springweb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() throws InterruptedException {
        log.info("TestController.test");
        Thread.sleep(2_000);
        return "Hello, World!";
    }
}
