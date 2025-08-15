package com.woopaca.laboratory.springweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class FullRequestLoggingConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setIncludeClientInfo(true);

        filter.setMaxPayloadLength(100_000);

        filter.setBeforeMessagePrefix("=== REQUEST START ===\n");
        filter.setAfterMessagePrefix("=== REQUEST END ===\n");

        return filter;
    }
}