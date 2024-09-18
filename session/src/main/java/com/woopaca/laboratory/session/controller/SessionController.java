package com.woopaca.laboratory.session.controller;

import com.woopaca.laboratory.session.dto.SessionRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequestMapping("/session")
@RestController
public class SessionController {

    private static final String SESSION_KEY = "session";

    @PostMapping
    public String registerSession(@RequestBody SessionRequest sessionRequest, HttpSession httpSession) {
        httpSession.setAttribute(SESSION_KEY, sessionRequest);
        return "Session registration success";
    }

    @GetMapping
    public String getSessionId(HttpSession httpSession, @CookieValue("test") String test) {
        SessionRequest attribute = Optional.ofNullable((SessionRequest) httpSession.getAttribute(SESSION_KEY))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Session ID"));
        log.info("attribute = {}", attribute);

        httpSession.setMaxInactiveInterval(5);
        return attribute.value();
    }
}
