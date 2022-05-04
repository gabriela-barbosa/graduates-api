package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.dto.RegisterDTO
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.service.UserService
import br.uff.graduatesapi.service.WorkHistoryService
import org.springframework.http.ResponseEntity
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api/v1")
class WorkHistoryController(private val workHistoryService: WorkHistoryService) {

    @GetMapping("workhistory/{id}")
    fun getWorkHistory(@PathVariable id: Int): ResponseEntity<Any> {

        val workHistory = this.workHistoryService.getWorkHistory(id)

        return ResponseEntity.ok(workHistory)
    }
}