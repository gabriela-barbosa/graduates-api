package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.WorkHistoryService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1")
class WorkHistoryController(
  private val workHistoryService: WorkHistoryService,
) {
  @PreAuthorize("isAuthenticated()")
  @GetMapping("workhistory/{id}")
  fun getWorkHistory(@PathVariable id: UUID): ResponseEntity<Any> {
    val workHistory = workHistoryService.getWorkHistoryDTO(id)
    return ResponseEntity.ok(workHistory)
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("workhistory/graduate/{id}")
  fun getWorkHistoryByGraduate(@PathVariable id: UUID): ResponseEntity<Any> {
    return when (val result = workHistoryService.getLastWorkHistoryByGraduate(id)) {
      is ResponseResult.Success -> ResponseEntity.ok(result)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("workhistory")
  fun createGraduateWorkHistory(@RequestBody workDTO: WorkHistoryDTO): ResponseEntity<String> {
    return when (val result = workHistoryService.createGraduateHistory(workDTO)) {
      is ResponseResult.Success -> ResponseEntity.status(201).build()
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping("workhistory/{id}")
  fun updateWorkHistory(
    @PathVariable id: UUID,
    @RequestBody workDTO: WorkHistoryDTO
  ): ResponseEntity<String> {
    return when (val result = workHistoryService.createGraduateHistory(workDTO, id)) {
      is ResponseResult.Success -> ResponseEntity.noContent().build()
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }
}