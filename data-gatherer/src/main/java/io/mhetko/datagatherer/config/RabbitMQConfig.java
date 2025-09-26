package io.mhetko.datagatherer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    @Bean
    TopicExchange fxExchange(@Value("${amqp.exchange}") String name) {
        return new TopicExchange(name, true, false); // durable
    }

    @Bean
    Queue rateChangedQueue(@Value("${amqp.queue}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    Binding fxBinding(Queue rateChangedQueue,
                      TopicExchange fxExchange,
                      @Value("${amqp.routing-key}") String rk) {
        return BindingBuilder.bind(rateChangedQueue).to(fxExchange).with(rk);
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
