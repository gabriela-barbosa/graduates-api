package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.GetAuthenticatedUser
import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.dto.toGetAuthenticatedUser
import br.uff.graduatesapi.dto.toGetUserDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.security.JWTUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
  private val userService: UserService,
  private val jwtUtil: JWTUtil,
  private val passwordEncoder: BCryptPasswordEncoder,
  ) {
  fun login(loginDTO: LoginDTO): ResponseResult<GetAuthenticatedUser> {


    val user = when (val resultUser = this.userService.findByEmail(loginDTO.email)) {
      is ResponseResult.Success -> resultUser.data!!
      is ResponseResult.Error -> return ResponseResult.Error(Errors.UNAUTHORIZED)
    }

    if (!user.comparePassword(loginDTO.password)) {
      return ResponseResult.Error(Errors.UNAUTHORIZED)
    }

    val userDTO = user.toGetUserDTO(user.currentRoleEnum ?:user.roles.sorted()[0])


    val issuer = user.id.toString()
    val token = jwtUtil.generateJWT(issuer)

    val result = userDTO.toGetAuthenticatedUser(token)

    return ResponseResult.Success(result)
  }
}