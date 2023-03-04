package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils.Companion.getRandomString
import br.uff.graduatesapi.dto.GetUsersDTO
import br.uff.graduatesapi.dto.RegisterDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.UserRepository
import br.uff.graduatesapi.security.JWTUtil
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtil: JWTUtil,
) {
    fun findByEmail(email: String): ResponseResult<PlatformUser> {
        val result = this.userRepository.findByEmail(email) ?: return ResponseResult.Error(Errors.USER_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun createUpdateUser(userDTO: RegisterDTO): ResponseResult<PlatformUser> {
        try {
            val user: PlatformUser
            if (userDTO.id != null) {
                val userResp = this.getById(userDTO.id)
                if (userResp is ResponseResult.Success) {
                    val userUpdate = userResp.data!!
                    if (userDTO.email != userUpdate.email && this.findByEmail(userDTO.email) is ResponseResult.Success) {
                        return ResponseResult.Error(Errors.EMAIL_IN_USE)
                    }
                    userUpdate.email = userDTO.email
                    userUpdate.name = userDTO.name
                    userUpdate.roles = userDTO.role
                    if (userDTO.password != null)
                        userUpdate.password = passwordEncoder.encode(userDTO.password)
                    user = userUpdate
                } else {
                    return ResponseResult.Error(Errors.CANT_CREATE_USER)
                }
            } else {
                if (this.findByEmail(userDTO.email) is ResponseResult.Success) {
                    return ResponseResult.Error(Errors.EMAIL_IN_USE)
                }
                user = PlatformUser(
                    name = userDTO.name,
                    email = userDTO.email,
                    roles = userDTO.role,
                )
                user.password = passwordEncoder.encode(getRandomString(10))
            }
            return ResponseResult.Success(this.userRepository.save(user))
        } catch (ex: Exception) {
            return ResponseResult.Error(Errors.CANT_CREATE_USER)
        }
    }

    fun updateEmail(oldEmail: String, newEmail: String): ResponseResult<PlatformUser> {
        if (findByEmail(newEmail) is ResponseResult.Error)
            return ResponseResult.Error(Errors.EMAIL_IN_USE)
        val userUpdated = this.userRepository.updateEmail(newEmail, oldEmail)
            ?: return ResponseResult.Error(Errors.CANT_UPDATE_EMAIL)
        return ResponseResult.Success(userUpdated)
    }

    fun getById(id: UUID): ResponseResult<PlatformUser> {
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