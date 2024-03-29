package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CreateInstitutionTypeDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.toResponseEntity
import br.uff.graduatesapi.service.InstitutionTypeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1")
class InstitutionTypeController(private val institutionTypeService: InstitutionTypeService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("institution/type")
    fun getInstitutionType(): ResponseEntity<Any> =
        when (val result = this.institutionTypeService.findActiveTypes()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> result.toResponseEntity()
        }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("institution/type/{id}")
    fun deleteInstitutionType(@PathVariable id: UUID): ResponseEntity<Any> =
        when (val result = this.institutionTypeService.deleteType(id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> result.toResponseEntity()
        }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("institution/type")
    fun createInstitutionType(
        @RequestBody createInstitutionTypeDTO: CreateInstitutionTypeDTO
    ): ResponseEntity<Any> = when (val result = this.institutionTypeService.createType(createInstitutionTypeDTO)) {
        is ResponseResult.Success -> ResponseEntity.status(HttpStatus.CREATED).body("Tipo de instituição criada com sucesso")
        is ResponseResult.Error -> result.toResponseEntity()
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("institution/type/{id}")
    fun editInstitutionType(@RequestBody createInstitutionTypeDTO: CreateInstitutionTypeDTO, @PathVariable id: UUID): ResponseEntity<Any> =
        when (val result = this.institutionTypeService.updateType(createInstitutionTypeDTO, id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> result.toResponseEntity()
        }
}
