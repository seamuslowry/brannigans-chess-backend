package com.seamuslowry.branniganschess.backend.branniganschess.config

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("clean")
class FlywayConfig {
    @Bean
    fun cleanMigrationStrategy() = FlywayMigrationStrategy {
        it.clean()
        it.migrate()
    }
}