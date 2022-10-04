package br.uff.graduatesapi.controller

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.CreateEmailDTO
import br.uff.graduatesapi.dto.UpdateEmailDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.EmailFilters
import br.uff.graduatesapi.service.EmailService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1")
class EmailController(private val emailService: EmailService) {
  @PreAuthorize("isAuthenticated()")
  @GetMapping("emails")
  fun getEmails(
    @RequestParam(value = "name", required = false) name: String?,
    @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
    @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,

    ): ResponseEntity<Any> {
    val filters = EmailFilters(name = name)
    val pageSetting = Utils.convertPagination(page, pageSize)
    return when (val result = this.emailService.findAll(filters, pageSetting)) {
      is ResponseResult.Success -> ResponseEntity.ok(result.data)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }

//  @PreAuthorize("isAuthenticated()")
//  @DeleteMapping("email/{id}")
//  fun deleteCIPrograms(@PathVariable id: Int): ResponseEntity<Any> =
//    when (val result = this.emailService.deleteProgram(id)) {
//      is ResponseResult.Success -> ResponseEntity.noContent().build()
//      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
//        .body(result.errorReason.responseMessage)
//    }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("email/{id}")
  fun getEmail(@PathVariable id: Int): ResponseEntity<Any> =
    when (val result = this.emailService.findEmail(id)) {
      is ResponseResult.Success -> ResponseEntity.ok(result.data)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("email")
  fun createEmail(@RequestBody createEmailDTO: CreateEmailDTO): ResponseEntity<Any> =
    when (val result = this.emailService.createEmail(createEmailDTO)) {
      is ResponseResult.Success -> ResponseEntity.ok("Email criado com sucesso!")
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PreAuthorize("isAuthenticated()")
  @PutMapping("email/{id}")
  fun editEmail(@RequestBody updateEmailDTO: UpdateEmailDTO, @PathVariable id: Int): ResponseEntity<Any> =
    when (val result = this.emailService.editEmail(updateEmailDTO, id)) {
      is ResponseResult.Success -> ResponseEntity.noContent().build()
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
}