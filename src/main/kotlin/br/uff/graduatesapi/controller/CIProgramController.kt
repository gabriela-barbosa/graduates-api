package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CIProgramDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.CIProgramService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1")
class CIProgramController(private val ciProgramService: CIProgramService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("ci-programs")
    fun getCIPrograms(): ResponseEntity<Any> =
        when (val result = this.ciProgramService.findPrograms()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("ci-program/{id}")
    fun deleteCIPrograms(@PathVariable id: UUID): ResponseEntity<Any> =
        when (val result = this.ciProgramService.deleteProgram(id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("ci-program")
    fun createCIProgram(@RequestBody ciProgramDTO: CIProgramDTO): ResponseEntity<Any> =
        when (val result = this.ciProgramService.createProgram(ciProgramDTO)) {
            is ResponseResult.Success -> ResponseEntity.status(HttpStatus.CREATED).body("Programa criado com sucesso")
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("ci-program/{id}")
    fun editCIProgram(@RequestBody ciProgramDTO: CIProgramDTO, @PathVariable id: UUID): ResponseEntity<Any> =
        when (val result = this.ciProgramService.editProgram(ciProgramDTO, id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
}