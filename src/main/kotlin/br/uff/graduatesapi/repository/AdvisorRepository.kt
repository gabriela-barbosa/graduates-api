package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.PlatformUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AdvisorRepository : JpaRepository<Advisor, UUID> {
    fun findAdvisorByUser(user: PlatformUser): Advisor?

    fun findAdvisorByUserNameIgnoreCase(name: String): Advisor?
}