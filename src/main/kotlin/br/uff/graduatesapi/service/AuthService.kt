package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.CIProgram
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.AdvisorRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.Cookie

@Service
class AuthService(
    private val userService: UserService,
    ) {

    fun login(loginDTO: LoginDTO): ResponseResult<Cookie?> {
        val resultUser = this.userService.findByEmail(loginDTO.email)
        if (resultUser is ResponseResult.Error)
            return ResponseResult.Error(resultUser.errorReason)
        val user = resultUser.data!!

        if (!user.comparePassword(loginDTO.password)) {
            return ResponseResult.Error(Errors.WRONG_PASSWORD)
        }

        val issuer = user.id.toString()
        val cookie = Utils.generateCookie(issuer)

        return ResponseResult.Success(cookie)
    }
}