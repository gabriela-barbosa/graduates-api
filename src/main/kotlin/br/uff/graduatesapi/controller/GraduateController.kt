package br.uff.graduatesapi.controller

import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.GraduateService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/v1")
class GraduateController(private val graduateService: GraduateService) {
  @PreAuthorize("isAuthenticated()")
  @GetMapping("graduate")
  fun getGraduatesByAdvisor(@CookieValue("jwt") jwt: String): ResponseEntity<Any>? {
    return when (val result = graduateService.getGraduatesByAdvisor(jwt)) {
      is ResponseResult.Success -> ResponseEntity.ok(result.data)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }
}