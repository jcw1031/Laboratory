package com.woopaca.laboratory.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth2.kakao")
public class KakaoOAuth2Properties {

    private String clientId;
    private String redirectUri;
}
