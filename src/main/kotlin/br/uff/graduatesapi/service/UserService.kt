package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.GetUsersDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.UserRepository
import br.uff.graduatesapi.security.JWTUtil
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
  private val userRepository: UserRepository,
  private val passwordEncoder: BCryptPasswordEncoder,
  private val jwtUtil: JWTUtil,
) {

  fun saveNewUser(user: PlatformUser): PlatformUser {
    user.password = passwordEncoder.encode(user.password)
    return this.userRepository.save(user)
  }

  fun findByEmail(email: String): ResponseResult<PlatformUser> {
    val result = this.userRepository.findByEmail(email) ?: return ResponseResult.Error(Errors.USER_NOT_FOUND)
    return ResponseResult.Success(result)
  }

  fun updateEmail(oldEmail: String, newEmail: String): ResponseResult<PlatformUser> {
    if (findByEmail(newEmail) is ResponseResult.Error)
      return ResponseResult.Error(Errors.EMAIL_IN_USE)
    val userUpdated = this.userRepository.updateEmail(newEmail, oldEmail)
      ?: return ResponseResult.Error(Errors.CANT_UPDATE_EMAIL)
    return ResponseResult.Success(userUpdated)
  }

  fun getById(id: Int): ResponseResult<PlatformUser> {
    return try {
      ResponseResult.Success(this.userRepository.getById(id))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.USER_NOT_FOUND)
    }
  }

  fun getUsers(pageable: Pageable): ResponseResult<GetUsersDTO> {
    return try {
      ResponseResult.Success(this.userRepository.findAllUsers(pageable)!!)
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.CANT_RETRIEVE_USERS)
    }
  }

  fun getUserByJwt(jwt: String): ResponseResult<PlatformUser> {
    try {
      val body = jwtUtil.parseJwtToBody(jwt)
      val result = getById(body.issuer.toInt())
      if (result is ResponseResult.Success)
        return ResponseResult.Success(result.data!!)
      return ResponseResult.Error(Errors.USER_NOT_FOUND)
    } catch (e: Exception) {
      return ResponseResult.Error(Errors.USER_NOT_FOUND)
    }
  }
}