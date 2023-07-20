package br.uff.graduatesapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {
  @Value("\${cors.originPatterns:default}")
  private val corsOriginPatterns: String = ""

  override fun addCorsMappings(registry: CorsRegistry) {
    val allowedOrigins = corsOriginPatterns.split(",").toTypedArray()
    registry.addMapping("/**")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
      .allowedOrigins(*allowedOrigins)
      .allowCredentials(true)
  }
}