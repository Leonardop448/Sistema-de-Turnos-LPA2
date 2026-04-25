package com.turnos.config;

import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder.applyToClusterSettings(clusterBuilder -> 
                clusterBuilder.serverSelectionTimeout(2000, TimeUnit.MILLISECONDS));
    }
}
