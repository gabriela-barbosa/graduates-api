package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.PlatformUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.lang.Nullable
import java.util.*

interface AdvisorRepository : JpaRepository<Advisor, UUID> {
    @Nullable
    fun findAdvisorByUser(user: PlatformUser): Advisor?

    @Nullable
    fun findAdvisorByUserNameIgnoreCase(name: String): Advisor?

}