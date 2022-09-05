package br.uff.graduatesapi.controller

import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.GraduateService
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.Query
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api/v1")
class GraduateController(private val graduateService: GraduateService) {
  @PreAuthorize("isAuthenticated()")
  @GetMapping("graduates")
  fun getGraduatesByAdvisor(
    @CookieValue("jwt") jwt: String,
    @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
    @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,
  ): ResponseEntity<Any>? {
    val pageSetting = PageRequest.of(page, pageSize)
    return when (val result = graduateService.getGraduatesByAdvisor(jwt, pageSetting)) {
      is ResponseResult.Success -> ResponseEntity.ok(result.data)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("allgraduates")
  fun getAllGraduate(
    @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
    @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,
    ): ResponseEntity<Any>? {
    val pageSetting = PageRequest.of(page, pageSize)
    return when (val result = graduateService.getAllGraduates(pageSetting)) {
      is ResponseResult.Success -> ResponseEntity.ok(result.data)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("graduate/{id}")
  fun getGraduateById(@PathVariable id: Int): ResponseEntity<Any>? {
    return when (val result = graduateService.getGraduateById(id)) {
      is ResponseResult.Success -> ResponseEntity.ok(result.data)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }
}