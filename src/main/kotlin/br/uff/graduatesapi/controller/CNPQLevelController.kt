package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CIProgramDTO
import br.uff.graduatesapi.dto.CNPQLevelDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.CNPQLevelService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1")
class CNPQLevelController(private val cnpqLevelService: CNPQLevelService) {
    @GetMapping("cnpqlevels")
    fun getCNPQLevels(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        return when (val result = this.cnpqLevelService.findCNPQLevels()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }

    @DeleteMapping("cnpqlevel/{id}")
    fun deleteCIPrograms(@CookieValue("jwt") jwt: String?, @PathVariable id: Int): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        return when (val result = this.cnpqLevelService.deleteCNPQLevel(id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }

    @PostMapping("cnpqlevel")
    fun createCIProgram(@CookieValue("jwt") jwt: String?, @RequestBody levelDTO: CNPQLevelDTO): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        return when (val result = this.cnpqLevelService.createLevel(levelDTO)) {
            is ResponseResult.Success -> ResponseEntity.ok("Programa criado com sucesso")
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }
}