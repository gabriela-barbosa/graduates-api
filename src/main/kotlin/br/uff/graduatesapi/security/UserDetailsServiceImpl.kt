package br.uff.graduatesapi.security

import br.uff.graduatesapi.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        if (username != null) {
            val user = userRepository.findByIdOrNull(UUID.fromString(username))
            if (user != null)
                return UserDetailsImpl(user)
        }
        return null
    }
}