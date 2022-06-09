package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CreateInstitutionTypeDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.InstitutionTypeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1")
class InstitutionTypeController(private val institutionTypeService: InstitutionTypeService) {
    @GetMapping("institution/type")
    fun getInstitutionType(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        return when (val result = this.institutionTypeService.findActiveTypes()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }
    @DeleteMapping("institution/type/{id}")
    fun deleteInstitutionType(@CookieValue("jwt") jwt: String?, @PathVariable id: Int): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        return when (val result = this.institutionTypeService.deleteType(id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }

    @PostMapping("institution/type")
    fun createInstitutionType(@CookieValue("jwt") jwt: String?, @RequestBody createInstitutionTypeDTO: CreateInstitutionTypeDTO): ResponseEntity<Any> {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        return when (val result = this.institutionTypeService.createType(createInstitutionTypeDTO)) {
            is ResponseResult.Success -> ResponseEntity.ok("Tipo de instituição criada com sucesso")
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }
}