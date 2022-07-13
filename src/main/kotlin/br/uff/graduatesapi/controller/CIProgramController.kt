package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CIProgramDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.CIProgramService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1")
class CIProgramController(private val ciProgramService: CIProgramService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("ciprograms")
    fun getCIPrograms(): ResponseEntity<Any> =
        when (val result = this.ciProgramService.findPrograms()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("ciprogram/{id}")
    fun deleteCIPrograms(@PathVariable id: Int): ResponseEntity<Any> =
        when (val result = this.ciProgramService.deleteProgram(id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("ciprogram")
    fun createCIProgram(@RequestBody ciProgramDTO: CIProgramDTO): ResponseEntity<Any> =
        when (val result = this.ciProgramService.createProgram(ciProgramDTO)) {
            is ResponseResult.Success -> ResponseEntity.ok("Programa criado com sucesso")
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
}