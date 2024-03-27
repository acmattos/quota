package br.com.acmattos.quota.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class UtcClockConfiguration {
    @Bean
    public Clock utcClock() {
        return Clock.system(ZoneId.of("UTC"));
    }
}
