package br.uff.graduatesapi.controller

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.toDTO
import br.uff.graduatesapi.entity.GraduateFilters
import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.toResponseEntity
import br.uff.graduatesapi.security.UserDetailsImpl
import br.uff.graduatesapi.service.AdvisorService
import br.uff.graduatesapi.service.GraduateService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@RestController
@RequestMapping("api/v1")
class AdvisorController(private val advisorService: AdvisorService) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("advisors")
	fun getAdvisorsFiltered(
		@AuthenticationPrincipal user: UserDetailsImpl,
		@RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
		@RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,
		@RequestParam(value = "name", required = false) name: String,
	): ResponseEntity<Any>? {
		return when (val result =
			advisorService.findAdvisorByNameLikeAndPaged(
				name, page, pageSize
			)) {
			is ResponseResult.Success -> ResponseEntity.ok(result.data)
			is ResponseResult.Error -> result.toResponseEntity()
		}
	}
}