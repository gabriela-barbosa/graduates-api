package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CNPQLevelDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.CNPQLevelService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1")
class CNPQController(private val cnpqLevelService: CNPQLevelService) {
  @PreAuthorize("isAuthenticated()")
  @GetMapping("cnpq_levels")
  fun getCNPQLevels(): ResponseEntity<Any> =
    when (val result = this.cnpqLevelService.findCNPQLevels()) {
      is ResponseResult.Success -> ResponseEntity.ok(result.data)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("cnpq_level/{id}")
  fun deleteCIPrograms(@PathVariable id: UUID): ResponseEntity<Any> =
    when (val result = this.cnpqLevelService.deleteCNPQLevel(id)) {
      is ResponseResult.Success -> ResponseEntity.noContent().build()
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("cnpq_level")
  fun createCIProgram(@RequestBody levelDTO: CNPQLevelDTO): ResponseEntity<Any> =
    when (val result = this.cnpqLevelService.createLevel(levelDTO)) {
      is ResponseResult.Success -> ResponseEntity.status(HttpStatus.CREATED).body("Programa criado com sucesso")
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PreAuthorize("isAuthenticated()")
  @PutMapping("cnpq_level/{id}")
  fun editCIProgram(@RequestBody levelDTO: CNPQLevelDTO, @PathVariable id: UUID): ResponseEntity<Any> =
    when (val result = this.cnpqLevelService.editLevel(levelDTO, id)) {
      is ResponseResult.Success -> ResponseEntity.noContent().build()
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
}