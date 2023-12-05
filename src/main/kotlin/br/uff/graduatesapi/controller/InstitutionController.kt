package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.CreateInstitutionDTO
import br.uff.graduatesapi.entity.InstitutionFilters
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.InstitutionService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1")
class InstitutionController(private val institutionService: InstitutionService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("institutions")
    fun getInstitution(
        @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,
        @RequestParam(value = "type", required = false) type: UUID?,
        @RequestParam(value = "name", required = false) name: String?,
    ): ResponseEntity<Any> {
        val filters = InstitutionFilters(
            name = name,
            type = type,
        )
        val pageSetting = PageRequest.of(page, pageSize)

        return when (val result = this.institutionService.findAllByFilters(filters, pageSetting)) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("institution")
    fun createInstitution(
        @RequestBody createInstitutionDTO: CreateInstitutionDTO
    ): ResponseEntity<Any> = when (val result = this.institutionService.createInstitution(createInstitutionDTO)) {
        is ResponseResult.Success -> ResponseEntity.ok("Instituição criada com sucesso")
        is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
            .body(result.errorReason.responseMessage)
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("institution/{id}")
    fun deleteInstitution(@PathVariable id: UUID): ResponseEntity<Any> =
        when (val result = this.institutionService.deleteInstitution(id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }


    @PreAuthorize("isAuthenticated()")
    @PutMapping("institution/{id}")
    fun editInstitutionType(
        @RequestBody createInstitutionDTO: CreateInstitutionDTO,
        @PathVariable id: UUID
    ): ResponseEntity<Any> =
        when (val result = this.institutionService.updateInstitution(createInstitutionDTO, id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }

}

