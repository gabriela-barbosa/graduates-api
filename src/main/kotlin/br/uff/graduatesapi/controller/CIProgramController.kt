package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.CIProgramService
import br.uff.graduatesapi.service.CNPQLevelService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1")
class CIProgramController(private val ciProgramService: CIProgramService) {
    @GetMapping("ciprograms")
    fun getCIPrograms(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        return when (val result = this.ciProgramService.findPrograms()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }
}