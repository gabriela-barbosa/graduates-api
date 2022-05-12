package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) {

    fun saveNewUser(user: PlatformUser): PlatformUser {
        user.password = passwordEncoder.encode(user.password)
        return this.userRepository.save(user)
    }

    fun update(user: PlatformUser): PlatformUser {
        return this.userRepository.save(user)
    }

    fun findByEmail(email: String): ResponseResult<PlatformUser> {
        val result = this.userRepository.findByEmail(email) ?: return ResponseResult.Error(Errors.USER_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun updateEmail(oldEmail: String, newEmail: String): ResponseResult<PlatformUser> {
        if (findByEmail(newEmail) != null)
            return ResponseResult.Error(Errors.EMAIL_IN_USE)
        val userUpdated = this.userRepository.updateEmail(newEmail, oldEmail)
            ?: return ResponseResult.Error(Errors.CANT_UPDATE_EMAIL)
        return ResponseResult.Success(userUpdated)
    }

    fun getById(id: Int): PlatformUser {
        return this.userRepository.getById(id)
    }

    fun getUserByJwt(jwt: String): PlatformUser? {
        return try {
            val body = Utils.parseJwtToBody(jwt)
            getById(body.issuer.toInt())
        } catch (e: Exception) {
            null
        }
    }
}