package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.GraduateService
import br.uff.graduatesapi.service.WorkHistoryService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1")
class WorkHistoryController(
    private val workHistoryService: WorkHistoryService,
    private val graduateService: GraduateService,
) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("workhistory/{id}")
    fun getWorkHistory(@PathVariable id: Int): ResponseEntity<Any> {
        val workHistory = workHistoryService.getWorkHistoryDTO(id)
        return ResponseEntity.ok(workHistory)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("workhistory/graduate/{id}")
    fun getWorkHistoryByGraduate(@PathVariable id: Int): ResponseEntity<Any> {
        val workHistory = workHistoryService.getLastWorkHistoryByGraduate(id)
        return ResponseEntity.ok(workHistory)
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("workhistory/{id}")
    fun updateWorkHistory(
        @PathVariable id: Int,
        @RequestBody workDTO: WorkHistoryDTO
    ): ResponseEntity<String> {
        return when (val result = graduateService.createGraduateWorkHistory(workDTO, id)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }
}