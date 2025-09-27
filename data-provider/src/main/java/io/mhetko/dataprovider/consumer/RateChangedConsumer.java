package io.mhetko.dataprovider.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RateChangedConsumer {

    @RabbitListener(queues = "${amqp.queue}")
    public void receiveMessage(Object payload) {
        System.out.println("Odebrano wiadomość: " + payload);
        // tutaj możesz dodać dalszą logikę przetwarzania
    }
}
