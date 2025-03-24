package com.woopaca.laboratory.springcloud.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class TestRoutes {

    @Bean
    public RouteLocator testRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("test_route", predicateSpec ->
                        predicateSpec.path("/test", "/test/test")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec.setRequestHeader(HttpHeaders.AUTHORIZATION, "test")
                                                .rewritePath("/api/(?<segment>.*)", "/${segment}"))
                                .uri("https://localhost:8081"))
                .build();
    }
}
