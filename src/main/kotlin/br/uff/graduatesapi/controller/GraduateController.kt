package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.dto.RegisterDTO
import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.service.GraduateService
import br.uff.graduatesapi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api/v1")
class GraduateController(private val graduateService: GraduateService) {
    @GetMapping("graduate")
    fun getGraduatesByAdvisor(@CookieValue("jwt") jwt: String?): ResponseEntity<Any>? {
        if (jwt == null) {
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        val graduates = graduateService.getGraduatesByAdvisor(jwt)

        return ResponseEntity.ok(graduates)
    }

    @PostMapping("graduate")
    fun createGraduateWorkHistory(@RequestBody workDTO: WorkHistoryDTO): ResponseEntity.BodyBuilder {
        graduateService.createGraduateWorkHistory(workDTO) ?: return ResponseEntity.status(400)
        return ResponseEntity.status(201)
    }
}