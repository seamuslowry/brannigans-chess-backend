package com.seamuslowry.branniganschess.backend.config

import com.seamuslowry.branniganschess.backend.security.AudienceValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.web.cors.CorsConfiguration


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
class WebSecurityConfig(
    @Value("\${cors.allowed-origin}") private val allowedOrigin: String,
    @Value("\${auth0.audience}") private val audience: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val issuer: String
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
                .authorizeRequests()
                    // websocket
                    .mvcMatchers("/ws/**").permitAll()
                    // swagger
                    .mvcMatchers("/swagger-resources/**", "/v2/api-docs", "/swagger-ui/**").permitAll()
                    // health check
                    .mvcMatchers("/health").permitAll()
                    // list of games
                    .mvcMatchers("/games").permitAll()
                    // any other request
                    .anyRequest().authenticated()
                .and().oauth2ResourceServer().jwt().decoder(jwtDecoder())
    }

    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator<Jwt>(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }
}