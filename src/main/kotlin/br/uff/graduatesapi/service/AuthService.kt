package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.security.JWTUtil
import org.springframework.stereotype.Service
import javax.servlet.http.Cookie

@Service
class AuthService(
    private val userService: UserService,
    private val jwtUtil: JWTUtil,
) {
    fun login(loginDTO: LoginDTO): ResponseResult<Cookie?> {


        val user = when (val resultUser = this.userService.findByEmail(loginDTO.email)) {
            is ResponseResult.Success -> resultUser.data!!
            is ResponseResult.Error -> return ResponseResult.Error(Errors.UNAUTHORIZED)
        }

        if (!user.comparePassword(loginDTO.password)) {
            return ResponseResult.Error(Errors.UNAUTHORIZED)
        }

        val issuer = user.id.toString()
        val cookie = jwtUtil.generateJWTCookie(issuer)

        return ResponseResult.Success(cookie)
    }
}