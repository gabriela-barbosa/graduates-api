package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Institution
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InstitutionRepository : JpaRepository<Institution, UUID> {
    fun findByNameAndTypeId(name: String, id: UUID) : Institution?
}