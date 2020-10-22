package com.seamuslowry.branniganschess.backend.config

import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.services.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ResourceServerConfig(
        @Value("\${cors.allowed-origin}") private val allowedOrigin: String,
        @Autowired private val playerService: PlayerService
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
                    .antMatchers("/game*").permitAll()
                    .anyRequest().authenticated()
                .and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(grantedAuthoritiesExtractor())
    }

    private fun grantedAuthoritiesExtractor(): JwtAuthenticationConverter {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { jwt ->
            val player = playerService.getOrCreate(jwt.subject)
            val whiteGames = playerService.getGames(player, PieceColor.WHITE, true)
            val blackGames = playerService.getGames(player, PieceColor.BLACK, true)

            whiteGames
                .map { SimpleGrantedAuthority("WHITE_${it.id}") }
                .plus(
                    blackGames
                        .map {
                            SimpleGrantedAuthority("BLACK_${it.id}")
                        })
        }

        return jwtAuthenticationConverter
    }
}