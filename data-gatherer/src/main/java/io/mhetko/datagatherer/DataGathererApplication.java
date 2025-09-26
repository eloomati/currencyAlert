package io.mhetko.datagatherer;

import io.mhetko.datagatherer.config.ProviderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ProviderProperties.class)
public class DataGathererApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataGathererApplication.class, args);
    }

}
