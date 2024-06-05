package com.woopaca.laboratory.stomp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class RabbitMQConfiguration {

    @Bean
    public Queue databaseQueue(RabbitMQProperties rabbitMQProperties) {
        return new Queue(rabbitMQProperties.getDatabaseQueueName());
    }

    @Bean
    public TopicExchange topicExchange(RabbitMQProperties rabbitMQProperties) {
        return new TopicExchange(rabbitMQProperties.getTopicExchangeName());
    }

    @Bean
    public Binding binding(Queue databaseQueue, TopicExchange topicExchange, RabbitMQProperties rabbitMQProperties) {
        return BindingBuilder.bind(databaseQueue)
                .to(topicExchange)
                .with(rabbitMQProperties.getRoutingKey());
    }
}
