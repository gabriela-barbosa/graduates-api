package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.UserRepository
import io.jsonwebtoken.Jwts
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) {

    fun save(user: PlatformUser): PlatformUser {
        user.password = passwordEncoder.encode(user.password)
        return this.userRepository.save(user)
    }

    fun findByEmail(email: String): PlatformUser? {
        return this.userRepository.findByEmail(email)
    }

    fun updateEmail(oldEmail: String, newEmail: String) {
        return this.userRepository.updateEmail(newEmail, oldEmail)
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