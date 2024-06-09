package com.woopaca.laboratory.security.controller;

import com.woopaca.laboratory.security.client.KakaoOAuth2Client;
import com.woopaca.laboratory.security.client.KakaoOAuth2Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/oauth2")
@RestController
public class OAuth2Controller {

    private final KakaoOAuth2Client kakaoOAuth2Client;

    public OAuth2Controller(KakaoOAuth2Client kakaoOAuth2Client) {
        this.kakaoOAuth2Client = kakaoOAuth2Client;
    }

    @GetMapping("/kakao")
    public void loginKakao(@RequestParam("code") String code) {
        KakaoOAuth2Token token = kakaoOAuth2Client.getToken(code);
        log.info("token = {}", token);
    }

}
