package io.mhetko.datagatherer.scheduler;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
public class RateUpdateScheduler {

    @Scheduled(cron = "${dg.fetch-cron:0 0 * * * ?}")
    public void updateRates(){
        System.out.println("Updating rates...");
    }
}
