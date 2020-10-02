package com.seamuslowry.branniganschess.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
        @Value("\${cors.allowed-origin}") private val allowedOrigin: String
): WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedOrigins(*allowedOrigin.split(",").toTypedArray())
        super.addCorsMappings(registry)
    }
}