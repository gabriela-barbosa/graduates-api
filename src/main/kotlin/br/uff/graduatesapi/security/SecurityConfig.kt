package br.uff.graduatesapi.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtUtil: JWTUtil,
    private val userDetailsService: UserDetailsService,
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable().authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
            .anyRequest().authenticated()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.addFilterBefore(
            JWTAuthorizationFilter(
                jwtUtil = jwtUtil,
                userDetailsService = userDetailsService,
            ),
            UsernamePasswordAuthenticationFilter::class.java,
        )
        http.cors(Customizer.withDefaults())
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}