package br.uff.graduatesapi.controller

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.dto.Message
import br.uff.graduatesapi.dto.RegisterDTO
import br.uff.graduatesapi.dto.UpdateCurrentRoleDTO
import br.uff.graduatesapi.entity.UserFilters
import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.security.UserDetailsImpl
import br.uff.graduatesapi.service.AuthService
import br.uff.graduatesapi.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api/v1")
class AuthController(
  private val userService: UserService,
  private val authService: AuthService,
) {

  @PostMapping("register")
  fun register(@RequestBody body: RegisterDTO): ResponseEntity<Any> =
    when (val result = this.userService.createUpdateUser(body)) {
      is ResponseResult.Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.data!!)
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PostMapping("login")
  fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
    return when (val result = authService.login(body)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok().body(result.data!!)
      }

      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(Message(result.errorReason.responseMessage))
    }
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping("user/current_role")
  fun updateCurrentRole(
    @AuthenticationPrincipal user: UserDetailsImpl,
    @RequestBody body: UpdateCurrentRoleDTO,
    response: HttpServletResponse
  ): ResponseEntity<Any> {
    return when (val result = userService.updateCurrentRole(user, body.currentRole)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok().body(result.data!!)
      }
      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(Message(result.errorReason.responseMessage))
    }
  }


  @PreAuthorize("isAuthenticated()")
  @GetMapping("user/{id}")
  fun getUserById(@PathVariable id: UUID): ResponseEntity<Any> =
    when (val result = this.userService.getById(id)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok().body(result.data!!)
      }

      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("user")
  fun getUserByJwt(@RequestHeader(name="Authorization") bearer: String): ResponseEntity<Any> {
    val jwt = Utils.getBearerToken(bearer)
    return when (val result = this.userService.getUserByJwt(jwt)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok().body(result.data!!)
      }

      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("users")
  fun getUsers(
    @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
    @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int,
    @RequestParam(value = "name", required = false) name: String?,
    @RequestParam(value = "email", required = false) email: String?,
    ): ResponseEntity<Any> {

    val filters = UserFilters(
      name = name,
      email = email,
    )

    val pageable: Pageable = PageRequest.of(page, pageSize)
    return when (val result = this.userService.getUsers(pageable, filters)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok().body(result.data!!)
      }

      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("users/role/{roleEnum}")
  fun getUsersByRole(@PathVariable roleEnum: RoleEnum): ResponseEntity<Any> {
    return when (val result = this.userService.getUsersByRole(roleEnum)) {
      is ResponseResult.Success -> {
        ResponseEntity.ok().body(result.data!!)
      }

      is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
        .body(result.errorReason.responseMessage)
    }
  }


  @PreAuthorize("isAuthenticated()")
  @PostMapping("logout")
  fun logout(response: HttpServletResponse): ResponseEntity<Any> {
    val cookie = Cookie("user.token", "")
    cookie.maxAge = 0
    response.addCookie(cookie)
    return ResponseEntity.ok(Message("Deslogado com sucesso!"))
  }
}