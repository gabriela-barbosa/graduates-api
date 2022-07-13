package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CNPQLevelDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.CNPQLevelService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1")
class CNPQLevelController(private val cnpqLevelService: CNPQLevelService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("cnpqlevels")
    fun getCNPQLevels(): ResponseEntity<Any> =
        when (val result = this.cnpqLevelService.findCNPQLevels()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("cnpqlevel/{id}")
    fun deleteCIPrograms(@PathVariable id: Int): ResponseEntity<Any> =
        when (val result = this.cnpqLevelService.deleteCNPQLevel(id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("cnpqlevel")
    fun createCIProgram(@RequestBody levelDTO: CNPQLevelDTO): ResponseEntity<Any> =
        when (val result = this.cnpqLevelService.createLevel(levelDTO)) {
            is ResponseResult.Success -> ResponseEntity.ok("Programa criado com sucesso")
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
}