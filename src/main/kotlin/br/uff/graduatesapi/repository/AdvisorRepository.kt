package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.PlatformUser
import org.springframework.data.jpa.repository.JpaRepository

interface AdvisorRepository : JpaRepository<Advisor, Int> {
    fun findAdvisorByUser(user: PlatformUser): Advisor?
}