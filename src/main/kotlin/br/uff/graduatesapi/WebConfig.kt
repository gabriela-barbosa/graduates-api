package br.uff.graduatesapi

import br.uff.graduatesapi.interceptor.AuthInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebConfig(private val interceptor: AuthInterceptor) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor)
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}