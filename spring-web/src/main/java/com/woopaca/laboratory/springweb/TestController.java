package com.woopaca.laboratory.springweb;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/test")
    public String test(HttpServletRequest request) throws InterruptedException {
        log.info("TestController.test");
        String remoteUser = request.getRemoteUser();
        log.info("remoteUser: {}", remoteUser);
        String remoteHost = request.getRemoteHost();
        log.info("remoteHost: {}", remoteHost);
        String remoteAddr = request.getRemoteAddr();
        log.info("remoteAddr: {}", remoteAddr);
        String remotePort = String.valueOf(request.getRemotePort());
        log.info("remotePort: {}", remotePort);
        Thread.sleep(2_000);
        return "Hello, World!";
    }

    @GetMapping("/exception")
    public String exception() {
        log.info("TestController.exception");
        throw new RuntimeException("This is a test exception");
    }
}
