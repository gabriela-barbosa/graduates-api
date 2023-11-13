package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Institution
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InstitutionRepository : JpaRepository<Institution, UUID> {
    fun findByNameAndTypeId(name: String, id: UUID) : Institution?
    fun findByNameLike(name: String?) : List<Institution>

    fun findByNameContainingIgnoreCase(name: String?) : List<Institution>
    fun findByNameContainingIgnoreCaseAndType(name: String?, type: UUID?, pageable: Pageable) : List<Institution>
}