package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Institution
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.lang.Nullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InstitutionRepository : JpaRepository<Institution, UUID>, InstitutionRepositoryCustom {
    @Nullable
    fun findByNameAndTypeId(name: String, id: UUID): Institution?

    @Nullable
    fun findFirstByNameIgnoreCase(name: String): Institution?
}