package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils.Companion.getRandomString
import br.uff.graduatesapi.dto.GetUsersDTO
import br.uff.graduatesapi.dto.RegisterDTO
import br.uff.graduatesapi.enum.Role
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.UserRepository
import br.uff.graduatesapi.security.JWTUtil
import br.uff.graduatesapi.security.UserDetailsImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
  private val userRepository: UserRepository,
  private val passwordEncoder: BCryptPasswordEncoder,
  private val jwtUtil: JWTUtil,
) {
  fun findByEmail(email: String): ResponseResult<PlatformUser> {
    val user = this.userRepository.findByEmail(email) ?: return ResponseResult.Error(Errors.USER_NOT_FOUND)
    return ResponseResult.Success(user)
  }

  fun updateUser(id: UUID, userDTO: RegisterDTO): ResponseResult<PlatformUser> {
    try {
      val user = when (val userResp = this.getById(id)) {
        is ResponseResult.Success -> userResp.data!!
        is ResponseResult.Error -> return ResponseResult.Error(Errors.CANT_CREATE_USER)
      }

      if (userDTO.email != user.email && this.findByEmail(userDTO.email) is ResponseResult.Success) {
        return ResponseResult.Error(Errors.EMAIL_IN_USE)
      }

      user.email = userDTO.email
      user.name = userDTO.name
      user.roles = userDTO.roles

      if (userDTO.password != null)
        user.password = passwordEncoder.encode(userDTO.password)

      return ResponseResult.Success(this.userRepository.save(user))
    } catch (ex: Exception) {
      return ResponseResult.Error(Errors.CANT_CREATE_USER)
    }
  }

  fun createUser(userDTO: RegisterDTO): ResponseResult<PlatformUser> {
    try {
      if (this.findByEmail(userDTO.email) is ResponseResult.Success) {
        return ResponseResult.Error(Errors.EMAIL_IN_USE)
      }
      val user = PlatformUser(
        name = userDTO.name,
        email = userDTO.email,
        roles = userDTO.roles,
//                currentRole = userDTO.roles[0]
      )
      user.password = passwordEncoder.encode(getRandomString(10))
      val resp = this.userRepository.save(user)
      return ResponseResult.Success(resp)
    } catch (ex: Exception) {
      return ResponseResult.Error(Errors.CANT_CREATE_USER)
    }
  }

  fun createUpdateUser(userDTO: RegisterDTO): ResponseResult<PlatformUser> =
    if (userDTO.id != null) {
      this.updateUser(userDTO.id, userDTO)
    } else {
      this.createUser(userDTO)
    }

  fun updateEmail(userId: UUID, newEmail: String): ResponseResult<PlatformUser> {
    if (findByEmail(newEmail) is ResponseResult.Error)
      return ResponseResult.Error(Errors.EMAIL_IN_USE)
    val userUpdated = this.userRepository.updateEmail(userId, newEmail)
      ?: return ResponseResult.Error(Errors.CANT_UPDATE_EMAIL)
    return ResponseResult.Success(userUpdated)
  }

  fun updateCurrentRole(user: UserDetailsImpl, currentRole: Role): ResponseResult<Any> {
    val platformUser = userRepository.findByIdOrNull(UUID.fromString(user.username))
    if (platformUser != null && platformUser.roles.contains(currentRole)) {
      platformUser.currentRole = currentRole
      userRepository.save(platformUser)
      return ResponseResult.Success(platformUser)
    }
    return ResponseResult.Error(Errors.UNABLE_TO_UPDATE_CURRENT_ROLE)
  }

  fun updateUserEmailAndName(user: PlatformUser, email: String, name: String): ResponseResult<PlatformUser> =
    try {
      user.email = email
      user.name = name
      ResponseResult.Success(this.userRepository.save(user))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.CANT_UPDATE_EMAIL)
    }

  fun getById(id: UUID): ResponseResult<PlatformUser> {
    return try {
      val result = this.userRepository.getById(id)
      ResponseResult.Success(result)
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

  fun getUserByJwt(jwt: String): ResponseResult<PlatformUser> =
    try {
      val body = jwtUtil.parseJwtToBody(jwt)

      when (val result = getById(UUID.fromString(body.issuer))) {
        is ResponseResult.Success -> ResponseResult.Success(result.data!!)
        is ResponseResult.Error -> ResponseResult.Error(Errors.USER_NOT_FOUND)
      }
    } catch (e: Exception) {
      ResponseResult.Error(Errors.USER_NOT_FOUND)
    }
}