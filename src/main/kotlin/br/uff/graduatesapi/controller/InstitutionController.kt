package br.uff.graduatesapi.controller

import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.InstitutionService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
class InstitutionController(private val institutionService: InstitutionService) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("institutions")
	fun getInstitutionType(
		@RequestParam(value = "name", required = false) name: String?,
	): ResponseEntity<Any> =
		when (val result = this.institutionService.findAllByName(name)) {
			is ResponseResult.Success -> ResponseEntity.ok(result.data)
			is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
				.body(result.errorReason.responseMessage)
		}
}
