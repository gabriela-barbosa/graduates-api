package br.uff.graduatesapi.controller

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.entity.InstitutionFilters
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.InstitutionService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api/v1")
class InstitutionController(private val institutionService: InstitutionService) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("institutions")
	fun getInstitutionType(
		@RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
		@RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,
		@RequestParam(value = "institutionType", required = false) institutionType: UUID?,
		@RequestParam(value = "name", required = false) name: String?,
	): ResponseEntity<Any> {
		val filters = InstitutionFilters(
			name = name,
			institutionType = institutionType,
		)
		val pageSetting = PageRequest.of(page, pageSize)

		return when (val result = this.institutionService.findAllByFilters(filters, pageSetting)) {
			is ResponseResult.Success -> ResponseEntity.ok(result.data)
			is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
				.body(result.errorReason.responseMessage)
		}
	}
	}

