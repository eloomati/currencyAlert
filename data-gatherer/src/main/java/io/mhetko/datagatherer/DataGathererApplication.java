package io.mhetko.datagatherer;

import io.mhetko.datagatherer.config.CurrencyProperties;
import io.mhetko.datagatherer.config.ProviderProperties;
import io.mhetko.datagatherer.config.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ProviderProperties.class, CurrencyProperties.class, RabbitProperties.class})
public class DataGathererApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataGathererApplication.class, args);
    }

}
