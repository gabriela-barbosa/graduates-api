package br.uff.graduatesapi.controller

import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.dto.RegisterDTO
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.service.AuthService
import br.uff.graduatesapi.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
        ResponseEntity.ok(Message("Logado com sucesso!"))
      }
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("user")
  fun user(@CookieValue("jwt") jwt: String): ResponseEntity<Any> =
    when (val result = this.userService.getUserByJwt(jwt)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok(result.data)
      }
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("users")
  fun user(@RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
           @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,): ResponseEntity<Any> {

    val pageable: Pageable = PageRequest.of(page, pageSize)
    return when (val result = this.userService.getUsers(pageable)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok(result.data)
      }

      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("user/{id}")
  fun getUserById(@PathVariable id: Int): ResponseEntity<Any> =
    when (val result = this.userService.getById(id)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok(result.data)
      }
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
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
    return ResponseEntity.ok(Message("Deslogado com sucesso!"))
  }
}