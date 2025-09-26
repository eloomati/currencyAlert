package io.mhetko.datagatherer.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateChangedProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${amqp.exchange}")
    private String exchange;

    @Value("${amqp.routing-key}")
    private String routingKey;

    public void sendText(String text) {
        rabbitTemplate.convertAndSend(exchange, routingKey, text);
    }

    public void sendJson(Object payload) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}

