package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.GraduateService
import br.uff.graduatesapi.service.WorkHistoryService
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1")
class WorkHistoryController(
    private val workHistoryService: WorkHistoryService,
    private val graduateService: GraduateService,
) {
    @GetMapping("workhistory/{id}")
    fun getWorkHistory(@PathVariable id: Int): ResponseEntity<Any> {
        val workHistory = this.workHistoryService.getWorkHistoryDTO(id)
        return ResponseEntity.ok(workHistory)
    }

    @PutMapping("workhistory/{id}")
    fun updateWorkHistory(
        @PathVariable id: Int,
        @RequestBody workDTO: WorkHistoryDTO
    ): ResponseEntity<String> {
        workDTO.id = id
        return when (val result = graduateService.createGraduateWorkHistory(workDTO)) {
            is ResponseResult.Success -> ResponseEntity.noContent().build()
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }
}