package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.PlatformUser
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<PlatformUser, Int> {
    fun findByEmail(email: String): PlatformUser?
}