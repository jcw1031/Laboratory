package com.woopaca.laboratory.security.client;

public interface OAuth2Client {

    KakaoOAuth2Token getToken(String code);
}
