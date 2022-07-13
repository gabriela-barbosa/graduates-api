package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.dto.RegisterDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.service.AuthService
import br.uff.graduatesapi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api/v1")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService,
) {

    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<PlatformUser> {
        val user = PlatformUser(
            name = body.name,
            email = body.email
        )
        user.password = body.password

        return ResponseEntity.ok(this.userService.saveNewUser(user))
    }

    @PostMapping("login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        return when (val result = authService.login(body)) {
            is ResponseResult.Success -> {
                response.addCookie(result.data)
                ResponseEntity.ok(Message("Success!"))
            }
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String): ResponseEntity<Any> {
        val user = this.userService.getUserByJwt(jwt)

        return if (user == null) ResponseEntity.status(Errors.USER_NOT_FOUND.errorCode).body(Errors.USER_NOT_FOUND.responseMessage)
        else ResponseEntity.ok(user)
    }

//    @GetMapping("user")
//    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
//        try {
//            if (jwt == null) {
//                return ResponseEntity.status(401).body(Message("Unauthenticated"))
//            }
//            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
//
//            return ResponseEntity.ok(this.userService.getById(body.issuer.toInt()))
//        } catch (e: Exception) {
//            return ResponseEntity.status(401).body(Message("Unauthenticated"))
//        }
//    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity.ok(Message("Success!"))
    }
}