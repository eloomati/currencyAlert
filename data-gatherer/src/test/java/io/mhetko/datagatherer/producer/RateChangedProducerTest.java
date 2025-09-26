package io.mhetko.datagatherer.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RateChangedProducerTest {

    record RateChangedEvent(String base, String symbol, double rate) {}

    @Test
    void jsonConverter_serializesPayload() {
        var converter = new Jackson2JsonMessageConverter();

        var payload = new RateChangedEvent("EUR", "PLN", 4.23);
        MessageProperties props = new MessageProperties();

        Message msg = converter.toMessage(payload, props);

        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
        assertThat(msg.getMessageProperties().getContentType()).isEqualTo(MessageProperties.CONTENT_TYPE_JSON);
        assertThat(msg.getMessageProperties().getContentEncoding()).isEqualTo("UTF-8");

        assertThat(body).isEqualTo("{\"base\":\"EUR\",\"symbol\":\"PLN\",\"rate\":4.23}");
    }
}