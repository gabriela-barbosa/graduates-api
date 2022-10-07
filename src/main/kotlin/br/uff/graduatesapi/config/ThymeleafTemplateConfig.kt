package br.uff.graduatesapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.nio.charset.StandardCharsets

@Configuration
class ThymeleafTemplateConfig {

  fun emailTemplateResolver(): ClassLoaderTemplateResolver {
    val emailTemplateResolver = ClassLoaderTemplateResolver()
    emailTemplateResolver.prefix = "/email/"
    emailTemplateResolver.suffix = ".html"
    emailTemplateResolver.templateMode = TemplateMode.HTML
    emailTemplateResolver.characterEncoding = StandardCharsets.UTF_8.name();
    emailTemplateResolver.isCacheable = false;
    return emailTemplateResolver;
  }

  @Bean
  fun springTemplateEngine(): SpringTemplateEngine {
    val springTemplateEngine = SpringTemplateEngine()
    springTemplateEngine.addTemplateResolver(emailTemplateResolver())
    return springTemplateEngine
  }
}