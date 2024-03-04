package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.toGetResetPasswordCodeDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.toResponseEntity
import br.uff.graduatesapi.service.ResetPasswordCodeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api/v1")
class ResetPasswordCodeController(private val resetPasswordCodeService: ResetPasswordCodeService) {
	@GetMapping("reset-password-code/{id}")
	fun getResetPasswordCode(@PathVariable id: UUID): ResponseEntity<Any> =
		when (val result = this.resetPasswordCodeService.getResetPasswordCodeById(id)) {
			is ResponseResult.Success -> ResponseEntity.ok(result.data!!.toGetResetPasswordCodeDTO())
			is ResponseResult.Error -> result.toResponseEntity()
		}
}