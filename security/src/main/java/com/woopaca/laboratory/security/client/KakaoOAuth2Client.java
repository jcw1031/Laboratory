package com.woopaca.laboratory.security.client;

import com.woopaca.laboratory.security.config.KakaoOAuth2Properties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoOAuth2Client implements OAuth2Client {

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    private final RestClient restClient;
    private final KakaoOAuth2Properties oAuth2Properties;

    public KakaoOAuth2Client(RestClient restClient, KakaoOAuth2Properties oAuth2Properties) {
        this.restClient = restClient;
        this.oAuth2Properties = oAuth2Properties;
    }

    @Override
    public KakaoOAuth2Token getToken(String code) {
        MultiValueMap<String, String> requestBody = createRequestBody(code);
        return restClient.post()
                .uri(KAKAO_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .body(KakaoOAuth2Token.class);
    }

    private MultiValueMap<String, String> createRequestBody(String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", oAuth2Properties.getClientId());
        requestBody.add("redirect_uri", oAuth2Properties.getRedirectUri());
        requestBody.add("code", code);
        return requestBody;
    }
}
