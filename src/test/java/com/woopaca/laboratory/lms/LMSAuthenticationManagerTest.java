package com.woopaca.laboratory.lms;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class LMSAuthenticationManagerTest {

    @Autowired
    private LMSAuthenticationManager lmsAuthenticationManager;

    @Test
    void test() throws JsonProcessingException {
        String sessionId = lmsAuthenticationManager.seleniumLogin("201901689", "Chan!1057");
        String userId = lmsAuthenticationManager.getUserId(sessionId);
        log.info("userId = {}", userId);

        sessionId = lmsAuthenticationManager.seleniumLogin("201901689", "Chan!1057");
        userId = lmsAuthenticationManager.getUserId(sessionId);
        log.info("userId = {}", userId);

        sessionId = lmsAuthenticationManager.seleniumLogin("201901689", "Chan!1057");
        userId = lmsAuthenticationManager.getUserId(sessionId);
        log.info("userId = {}", userId);
    }
}