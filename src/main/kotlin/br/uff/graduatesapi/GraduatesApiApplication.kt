package br.uff.graduatesapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class GraduatesApiApplication

fun main(args: Array<String>) {
    runApplication<GraduatesApiApplication>(*args)
}
