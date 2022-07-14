package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.GraduateService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api/v1")
class GraduateController(private val graduateService: GraduateService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("graduate")
    fun getGraduatesByAdvisor(@CookieValue("jwt") jwt: String): ResponseEntity<Any>? {
        val graduates = graduateService.getGraduatesByAdvisor(jwt)
        return ResponseEntity.ok(graduates)
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("graduate")
    fun createGraduateWorkHistory(@RequestBody workDTO: WorkHistoryDTO): ResponseEntity<String> {
        return when (val result = graduateService.createGraduateWorkHistory(workDTO)) {
            is ResponseResult.Success -> ResponseEntity.status(201).build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }
}