package com.woopaca.laboratory.stomp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Profile("!test")
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private String topicExchangeName;
    private String databaseQueueName;
    private String routingKey;
}
