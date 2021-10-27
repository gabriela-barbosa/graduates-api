package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.UserRepository
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

    fun getById(id: Int): PlatformUser {
        return this.userRepository.getById(id)
    }
}