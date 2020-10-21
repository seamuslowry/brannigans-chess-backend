package com.seamuslowry.branniganschess.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class ResourceServerConfig(
        @Value("\${cors.allowed-origin}") private val allowedOrigin: String
): WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors().configurationSource {
                    val corsConfiguration = CorsConfiguration()
                    corsConfiguration.addAllowedMethod(CorsConfiguration.ALL)
                    corsConfiguration.addAllowedHeader(CorsConfiguration.ALL)
                    allowedOrigin.split(",").forEach { corsConfiguration.addAllowedOrigin(it) }
                    corsConfiguration.allowCredentials = true

                    corsConfiguration
                }
                .and().formLogin().disable()
                .logout().disable()
                .authorizeRequests().anyRequest().anonymous()
                .and().oauth2ResourceServer().jwt()
    }
}